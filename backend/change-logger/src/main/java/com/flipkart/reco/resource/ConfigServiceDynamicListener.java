package com.flipkart.reco.resource;

import com.flipkart.kloud.config.BaseBucketUpdateListener;
import com.flipkart.kloud.config.Bucket;
import com.flipkart.kloud.config.DynamicBucket;
import com.flipkart.kloud.config.error.ConfigServiceException;
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
            DynamicBucket bucket = ConfigListenerUtil.getInstance1().getConfigServiceDynamicBucket(configBucketName);
            ConfigListenerUtil.getInstance1().makeDBConsistent(configBucketName, zone, configUpdateDao);
            if(bucket != null) {
            bucket.addListener(new BaseBucketUpdateListener() {
                @SneakyThrows
                @Override
                public void updated(Bucket oldBucket, Bucket newBucket) {
                    int version = ConfigListenerUtil.getInstance1().getLatestVersion(configBucketName);
                    String json = ConfigListenerUtil.getInstance1().getUpdateByVersion(configBucketName,version);
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


