package com.flipkart.reco.util;

import com.flipkart.reco.resource.ConfigServiceDynamicListener;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ConfigListenerStorageUtil implements Storage<String, ConfigServiceDynamicListener> {

    private final Map<String, ConfigServiceDynamicListener> storage;
    @Getter
    private static ConfigListenerStorageUtil configListenerStorageUtilInstance = new ConfigListenerStorageUtil();
    public ConfigListenerStorageUtil() {
        storage = new HashMap<>();
    }

    @Override
    public void add(String key, ConfigServiceDynamicListener value) {
        storage.put(key, value);
    }

    @Override
    public ConfigServiceDynamicListener get(String key) {
        return storage.getOrDefault(key, null);
    }

    @Override
    public void remove(String key) {
        storage.remove(key);
    }
}