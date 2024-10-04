package com.flipkart.reco.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@IdClass(CompositeKey.class)
@Table(name="db")
public class DBEntity implements Serializable {
    @Id
    @Column(name = "name")
    private String name;

    @Id
    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Id
    @Column(name = "zones")
    private String zones;

    @Column(name = "version")
    private int version;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @Column(name = "msg", columnDefinition = "TEXT")
    private String msg;

    @Column(name = "change_data", columnDefinition = "TEXT")
    private String changeData; //Enum -> GCP, CH, HYD, ALL

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getChangeData() {
        return changeData;
    }

    public String getZones() {
        return zones;
    }

    public void setZones(String zones) {
        this.zones = zones;
    }

    public void setChangeData(String changeData) {
        this.changeData = changeData;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }



    public void setName(String id) {
        this.name = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "name :" + name + " version: " + version + " type: "+ type + " author: " + author + " msg: " + msg + " timestamp: " + timestamp + " changeData: " + changeData + " zones: " + zones;
    }

}
