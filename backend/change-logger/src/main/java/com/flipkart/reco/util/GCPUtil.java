package com.flipkart.reco.util;

import com.flipkart.kloud.config.*;
import com.flipkart.kloud.config.error.ConfigServiceException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class GCPUtil {
    private final ExecutorService executorService;
    private final DynamicBucketCache dynamicBucketCache;
    private final HttpClient httpClient;
    @Getter
    static GCPUtil instance = new GCPUtil();
    @Getter
    Map<String, Map<String, ConfigServiceEndpoint>> endpoints;
    private GCPUtil() {
        executorService = Executors.newFixedThreadPool(20,
                r -> new Thread(r, "ConfigWatchThreadGCP"));
        dynamicBucketCache = new DynamicBucketCache();
        InstanceMetaData instanceMetaData = InstanceMetaData.singleton();
        endpoints = EndpointProvider.ZONE_VPC_TO_ENDPOINT_MAP;
        ConfigServiceEndpoint configServiceEndpoint = endpoints.get("asia-south1").get("default");
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
}
