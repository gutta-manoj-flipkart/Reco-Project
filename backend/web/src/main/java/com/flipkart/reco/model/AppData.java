package com.flipkart.reco.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class AppData {
    private String appid;
    private Map<String, String> buckets;
}
