package com.flipkart.reco.util;

import com.flipkart.kloud.config.*;
import com.flipkart.kloud.config.error.ConfigServiceException;
import com.flipkart.security.cryptex.CryptexClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConfigServiceUtil {

    private final ExecutorService executorService;
    private final DynamicBucketCache dynamicBucketCache;
    private final HttpClient httpClient;
    private final CryptexClient cryptexClient = null;
    private static final String CRYPTEX_CLIENT_ENDPOINT = "https://service.cryptex-prod.fkcloud.in";

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
        //cryptexClient = new CryptexClientBuilder().endpoint(CRYPTEX_CLIENT_ENDPOINT).build();
    }


    /** Returns Dynamic bucket of the given bucket name. Do not use this function in the config
     * parser class directly*/
    public DynamicBucket getConfigServiceDynamicBucket(String bucketName) {
        DynamicBucket dynamicBucket = null;
        try {
            dynamicBucket = dynamicBucketCache.getDynamicBucket(bucketName, httpClient, executorService);//, cryptexClient);
            long delayBetweenRetry = 200; // Delay in milliseconds between the first attempt and the retry

            // Check if the first attempt was unsuccessful
            if (dynamicBucket == null) {
                System.out.println("Dynamic bucket is null for bucket name: {0}. Retrying once after delay...");

                // Delay before retrying
                try {
                    TimeUnit.MILLISECONDS.sleep(delayBetweenRetry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                   System.out.println("Thread interrupted while waiting for retry delay.");
                    return null;
                }
                dynamicBucket = dynamicBucketCache.getDynamicBucket(bucketName, httpClient, executorService);//, cryptexClient);
            }

            if (dynamicBucket == null) {
                System.out.println( "Dynamic bucket is still null after retry for bucket name: {0}.");
                return null;
            }
            return dynamicBucket;
        } catch (ConfigServiceException e) {
            log.error("Error fetching bucket {}", bucketName, e);
        }
        return dynamicBucket;
    }
}
