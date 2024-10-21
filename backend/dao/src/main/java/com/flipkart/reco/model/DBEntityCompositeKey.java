package com.flipkart.reco.model;

import lombok.Getter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
public class DBEntityCompositeKey implements Serializable {
    private String name;

    private Timestamp timestamp;

    private String zones;

    public DBEntityCompositeKey() {}
    public DBEntityCompositeKey(String name, Timestamp timestamp, String zones) {
        this.name = name;
        this.timestamp = timestamp;
        this.zones = zones;
    }
}
