package com.reco.poller.util;

import com.flipkart.kloud.config.ConfigServiceEndpoint;
import com.flipkart.kloud.config.DynamicBucket;
import com.flipkart.kloud.config.DynamicBucketCache;
import com.flipkart.kloud.config.EndpointProvider;
import com.flipkart.kloud.config.HttpClient;
import com.flipkart.kloud.config.HttpClientBuilder;
import com.flipkart.kloud.config.InstanceMetaData;
import com.flipkart.kloud.config.error.ConfigServiceException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ConfigServiceUtil {

    private final ExecutorService executorService;
    private final DynamicBucketCache dynamicBucketCache;
    private final HttpClient httpClient;

    @Getter
    static ConfigServiceUtil instance = new ConfigServiceUtil();

    private ConfigServiceUtil() {

        executorService = Executors.newFixedThreadPool(20,
                r -> new Thread(r, "ConfigWatchThread"));
        dynamicBucketCache = new DynamicBucketCache();
        InstanceMetaData instanceMetaData = InstanceMetaData.singleton();
        ConfigServiceEndpoint configServiceEndpoint = EndpointProvider.singleton().endpoint();
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
            dynamicBucket = dynamicBucketCache.getDynamicBucket(bucketName, httpClient,
                    executorService);
        } catch (ConfigServiceException e) {
            log.error("Error fetching bucket {}", bucketName, e);
        }
        return dynamicBucket;
    }
}
