package com.flipkart.reco.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AppEntity {
    @Column(name="app_id")
    private String appId;

    @Column(name = "system_type")
    private String system;

    @Id
    private String name;

    private String zone;

    public AppEntity() {}
    public AppEntity(String appId, String system, String name, String zone) {
        this.appId = appId;
        this.system = system;
        this.name = name;
        this.zone = zone;
    }
}
