package com.flipkart.reco.model;

import lombok.Getter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
public class CompositeKey implements Serializable {
    private String name;

    private Timestamp timestamp;

    private String zones;

    public CompositeKey() {}
    public CompositeKey(String name, Timestamp timestamp, String zones) {
        this.name = name;
        this.timestamp = timestamp;
        this.zones = zones;
    }
}
