package com.flipkart.reco.util;

import com.flipkart.kloud.config.*;
import com.flipkart.kloud.config.error.ConfigServiceException;
import com.flipkart.reco.service.ConfigUpdateDao;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ConfigListenerUtil {
    private final ExecutorService executorService;
    private final DynamicBucketCache dynamicBucketCache;
    private final Client client;
    private final String url;
    private final HttpClient httpClient;
    @Getter
    static ConfigListenerUtil instance1 = new ConfigListenerUtil();
    private ConfigListenerUtil() {
        client = ClientBuilder.newClient();
        executorService = Executors.newFixedThreadPool(20,
                r -> new Thread(r, "ConfigWatchThread"));
        dynamicBucketCache = new DynamicBucketCache();
        InstanceMetaData instanceMetaData = InstanceMetaData.singleton();
        ConfigServiceEndpoint configServiceEndpoint = EndpointProvider.singleton().endpoint();
        url =  "http://"+ configServiceEndpoint.host + "/v1/history/";
        httpClient = new HttpClientBuilder().host(configServiceEndpoint.host)
                .port(configServiceEndpoint.port).apiVersion(configServiceEndpoint.apiVersion)
                .connectTimeout(5000).executorService(executorService).connections(20)
                .instanceMetaData(instanceMetaData).kmsTypingEnabled(false).build();
    }

    /** Returns Dynamic bucket of the given bucket name. Do not use this function in the config
     * parser class directly*/
    public DynamicBucket getConfigServiceDynamicBucket(String bucketName) {
        DynamicBucket dynamicBucket = null;
        try {
            dynamicBucket = dynamicBucketCache.getDynamicBucket(bucketName, httpClient, executorService);
            return dynamicBucket;
        } catch (ConfigServiceException e) {
            log.error("Error fetching bucket {}", bucketName, e);
        }
        return dynamicBucket;
    }

    public void makeDBConsistent(String name, String zone, ConfigUpdateDao configUpdateDao) throws ParseException {
        int version = getLatestVersion(name);
        int db_version;
        Integer v = configUpdateDao.findHighestVersionByName(name);
        db_version = Objects.requireNonNullElse(v, 0);
        db_version++;
        while(db_version != version+1)
        {
            String json1 = getUpdateByVersion(name,db_version);
            try {
                configUpdateDao.save(name,zone,json1);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            db_version++;
        }
    }

    public int getLatestVersion(String configBucketName) throws ParseException {
        String json = getLatestUpdate(configBucketName);
        JSONParser parser = new JSONParser();
        JSONArray arr = (JSONArray) parser.parse(json);
        JSONObject obj = (JSONObject) arr.get(0);
        return ((Long) obj.get("version")).intValue();
    }
    public String getLatestUpdate(String configBucketName){
        WebTarget target = client.target(url).queryParam("limit", "1").path(configBucketName);
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        return response.readEntity(String.class);
    }
    public String getUpdateByVersion(String configBucketName,int version) {
        WebTarget target = client.target(url).path(configBucketName).path(String.valueOf(version));
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        return response.readEntity(String.class);
    }
}
