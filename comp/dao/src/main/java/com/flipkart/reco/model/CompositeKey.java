package com.flipkart.reco.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class CompositeKey implements Serializable {
    private String name;

    private Timestamp timestamp;

    private String zones;

    // default constructor
    public CompositeKey()
    {

    }
    public CompositeKey(String name, Timestamp timestamp, String zones) {
        this.name = name;
        this.timestamp = timestamp;
        this.zones = zones;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZones() {
        return zones;
    }

    public void setZones(String zones) {
        this.zones = zones;
    }

    // equals() and hashCode()
}
