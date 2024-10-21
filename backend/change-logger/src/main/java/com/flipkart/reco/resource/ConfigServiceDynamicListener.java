package com.flipkart.reco.resource;

import com.flipkart.kloud.config.BaseBucketUpdateListener;
import com.flipkart.kloud.config.Bucket;
import com.flipkart.kloud.config.DynamicBucket;
import com.flipkart.kloud.config.error.ConfigServiceException;
import com.flipkart.reco.service.ConfigService;
import com.flipkart.reco.service.ConfigUpdateDao;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.util.ConfigListenerUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;

@Slf4j
public class ConfigServiceDynamicListener {
    public ConfigServiceDynamicListener(String configBucketName, String zone, ConfigUpdateRepository configUpdateRepository) {
        ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
        try {
            DynamicBucket bucket = ConfigListenerUtil.getInstance().getConfigServiceDynamicBucket(configBucketName);
            String url = "http://"+ ConfigListenerUtil.getInstance().getEndpoints().get("asia-south1").get("default").host + "/v1/history/";
            ConfigService configService = new ConfigService();
            configService.makeDBConsistent(url, configBucketName, zone, configUpdateDao);
            if(bucket != null) {
            bucket.addListener(new BaseBucketUpdateListener() {
                @SneakyThrows
                @Override
                public void updated(Bucket oldBucket, Bucket newBucket) {
                    int version = configService.getLatestVersion(url,configBucketName);
                    String json = configService.getUpdateByVersion(url,configBucketName,version);
                    try {
                        configUpdateDao.save(configBucketName, zone, json);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            }
            else {
                log.error("Error in fetching config bucket {}", configBucketName);
            }
        }
        catch (ConfigServiceException | ParseException e) {
            log.error("Error while parsing config {} ", configBucketName, e);
        }
  }
}


