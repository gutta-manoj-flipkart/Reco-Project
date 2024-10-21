package com.flipkart.reco.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class DBEntityDTO {
    private String name;

    private Timestamp timestamp;

    private String zones;

    private int version;

    private String type;

    private String author;

    private String msg;

    private String changeData;

    public DBEntityDTO(String name, Timestamp timestamp,String zones, int version, String type, String author, String msg, String changeData) {
        this.name = name;
        this.timestamp = timestamp;
        this.zones = zones;
        this.version = version;
        this.type = type;
        this.author = author;
        this.msg = msg;
        this.changeData = changeData;
    }
}
