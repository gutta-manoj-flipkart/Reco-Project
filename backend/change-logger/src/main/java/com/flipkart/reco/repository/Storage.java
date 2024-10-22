package com.flipkart.reco.service;

public interface Storage<K, V> {
    void add(K key, V value);
    V get(K key);
    void remove(K key);
}

