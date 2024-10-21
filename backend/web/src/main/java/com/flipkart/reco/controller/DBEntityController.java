package com.flipkart.reco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flipkart.reco.model.*;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.repository.MetaDataRepository;
import com.flipkart.reco.service.ConfigUpdateDao;
import com.flipkart.reco.service.MetaDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
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
    public ResponseEntity<Object> getDetails(@RequestBody ConfigFilterData data) throws JsonProcessingException {
        ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
        List<DBEntityDTO> body = configUpdateDao.getEntriesBetweenTimestampsAndZones(new Timestamp(Long.parseLong(data.getFromDateTime_UNIX())),new Timestamp((Long.parseLong(data.getToDateTime_UNIX()))), data.getDc());
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
