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
}
