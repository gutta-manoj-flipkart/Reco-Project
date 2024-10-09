package com.flipkart.reco.listener;

import com.flipkart.kloud.config.BaseBucketUpdateListener;
import com.flipkart.kloud.config.Bucket;
import com.flipkart.kloud.config.DynamicBucket;
import com.flipkart.kloud.config.error.ConfigServiceException;
import com.flipkart.reco.service.ConfigUpdateDao;
import com.flipkart.reco.util.ConfigServiceUtil;
import com.flipkart.reco.repository.ConfigUpdateRepository;
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
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConfigServiceDynamicListener {
    public ConfigServiceDynamicListener(String configBucketName,ConfigUpdateRepository configUpdateRepository) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
            DynamicBucket bucket = ConfigServiceUtil.getInstance()
                    .getConfigServiceDynamicBucket(configBucketName);
            long delayBetweenRetry = 200;
            if (bucket == null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(delayBetweenRetry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread interrupted while waiting for retry delay.");
                }
                bucket = ConfigServiceUtil.getInstance()
                        .getConfigServiceDynamicBucket(configBucketName);
            }

            if (bucket == null) {
                System.out.println("This is still ain't working for " + configBucketName);
            }
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
            int db_version;
            Integer v = configUpdateRepository.findHighestVersionByName(configBucketName);
            if(v == null)
                db_version = 0;
            else
                db_version = v;
            db_version++;
            while(db_version != version+1)
            {
                Client client1 = ClientBuilder.newClient();
                WebTarget webTarget1 = client1.target(url);
                WebTarget target1 = webTarget1.path(configBucketName).path(String.valueOf(db_version));
                Invocation.Builder invocationBuilder1 = target1.request(MediaType.APPLICATION_JSON);
                Response response1 = invocationBuilder1.get();
                String json1 = response1.readEntity(String.class);
                ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
                try {
                    configUpdateDao.storetoDB(configBucketName,json1);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                db_version++;
            }
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
                        configUpdateDao.storetoDB(configBucketName,json1);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        }  catch (ConfigServiceException e) {
            log.error("Error while parsing config {} ", configBucketName, e);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
  }
}


