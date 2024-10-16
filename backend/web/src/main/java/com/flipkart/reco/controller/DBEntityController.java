package com.flipkart.reco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.reco.listener.ConfigServiceDynamicListener;
import com.flipkart.reco.model.AppData;
import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.repository.MetaDataRepository;
import com.flipkart.reco.service.ConfigUpdateDao;
import com.flipkart.reco.model.DBEntity;
import com.flipkart.reco.service.MetaDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class DBEntityController {
    @Autowired
    private ConfigUpdateRepository configUpdateRepository;
    @Autowired
    private MetaDataRepository metaDataRepository;

    private final RestTemplate restTemplate;
    @Autowired
    public DBEntityController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/getdetails")
    public ResponseEntity<Object> ping(@RequestBody String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
        List<DBEntity> body = configUpdateDao.getEntriesBetweenTimestamps(new Timestamp(node.get("fromDateTime_UNIX").asLong()),new Timestamp(node.get("toDateTime_UNIX").asLong()));
        return ResponseEntity.ok(body);
    }
    @PostMapping("/client")
    public String client(@RequestBody AppData appData) {
        MetaDataDao metaDataDao = new MetaDataDao(metaDataRepository);
        Map<String, String> buckets = appData.getBuckets();
        for (Map.Entry<String, String> entry : buckets.entrySet()) {
            metaDataDao.addUpdate(new AppEntity(appData.getAppid(), "bucket", entry.getKey(), entry.getValue()));
        }
        String url = "http://localhost:8081/test";
        restTemplate.getForEntity(url, String.class);
        return "Success";
    }
}
