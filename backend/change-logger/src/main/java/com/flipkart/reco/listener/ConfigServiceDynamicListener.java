package com.flipkart.reco.listener;

import com.flipkart.kloud.config.BaseBucketUpdateListener;
import com.flipkart.kloud.config.Bucket;
import com.flipkart.kloud.config.DynamicBucket;
import com.flipkart.kloud.config.error.ConfigServiceException;
import com.flipkart.reco.service.ConfigUpdateDao;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.util.GCPUtil;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class ConfigServiceDynamicListener {
    public ConfigServiceDynamicListener(String configBucketName, String zone, ConfigUpdateRepository configUpdateRepository) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
            DynamicBucket bucket = GCPUtil.getInstance().getConfigServiceDynamicBucket(configBucketName);
            if(bucket != null) {
            bucket.addListener(new BaseBucketUpdateListener() {
                @SneakyThrows
                @Override
                public void updated(Bucket oldBucket, Bucket newBucket) {
                    String url = "http://10.83.47.156/v1/history/";
                    Client client = ClientBuilder.newClient();
                    WebTarget webTarget = client.target(url).queryParam("limit", "1");
                    WebTarget target = webTarget.path(configBucketName);
                    Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
                    Response response = invocationBuilder.get();
                    String json = response.readEntity(String.class);
                    JSONParser parser = new JSONParser();
                    JSONArray arr = (JSONArray) parser.parse(json);
                    JSONObject obj = (JSONObject) arr.get(0);
                    int version = ((Long) obj.get("version")).intValue();
                    Client client1 = ClientBuilder.newClient();
                    WebTarget webTarget1 = client1.target(url);
                    WebTarget target1 = webTarget1.path(configBucketName).path(String.valueOf(version));
                    Invocation.Builder invocationBuilder1 = target1.request(MediaType.APPLICATION_JSON);
                    Response response1 = invocationBuilder1.get();
                    String json1 = response1.readEntity(String.class);
                    ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
                    try {
                        configUpdateDao.storetoDB(configBucketName, zone, json1);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
            else {
                log.error("Error in fetching config bucket {}", configBucketName);
            }
        }  catch (ConfigServiceException e) {
            log.error("Error while parsing config {} ", configBucketName, e);
        }
  }
}


