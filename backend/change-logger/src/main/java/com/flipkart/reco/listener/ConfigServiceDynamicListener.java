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
@Slf4j
public class ConfigServiceDynamicListener {
    public ConfigServiceDynamicListener(String configBucketName, String zone, ConfigUpdateRepository configUpdateRepository) {
        try {
            DynamicBucket bucket = GCPUtil.getInstance().getConfigServiceDynamicBucket(configBucketName);
            String url = "http://"+ GCPUtil.getInstance().getEndpoints().get("asia-south1").get("default").host + "/v1/history/";
            Client client = ClientBuilder.newClient();
            if(bucket != null) {
            bucket.addListener(new BaseBucketUpdateListener() {
                @SneakyThrows
                @Override
                public void updated(Bucket oldBucket, Bucket newBucket) {
                    int version =  getLatestVersion(client,url,configBucketName);
                    String json = getUpdateByVersion(client,url,configBucketName,version);
                    ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
                    try {
                        configUpdateDao.storetoDB(configBucketName, zone, json);
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
  private int getLatestVersion(Client client,String url, String configBucketName) throws ParseException {
      String json = getLatestUpdate(client,url,configBucketName);
      JSONParser parser = new JSONParser();
      JSONArray arr = (JSONArray) parser.parse(json);
      JSONObject obj = (JSONObject) arr.get(0);
      return ((Long) obj.get("version")).intValue();
  }
  private String getLatestUpdate(Client client,String url, String configBucketName){
      WebTarget target = client.target(url).queryParam("limit", "1").path(configBucketName);
      Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
      Response response = invocationBuilder.get();
      return response.readEntity(String.class);
  }
  private String getUpdateByVersion(Client client,String url, String configBucketName,int version) {
        WebTarget target = client.target(url).path(configBucketName).path(String.valueOf(version));
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        return response.readEntity(String.class);
  }
}


