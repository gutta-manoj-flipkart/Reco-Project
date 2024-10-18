package com.flipkart.reco.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@IdClass(CompositeKey.class)
@Table(name="db")
@Getter
@Setter
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

    @Lob
    @Column(name = "change_data")
    private byte[] changeData;

    @Override
    public String toString() {
        return "name :" + name + " version: " + version + " type: "+ type + " author: " + author + " msg: " + msg + " timestamp: " + timestamp + " changeData: " + changeData + " zones: " + zones;
    }

}
