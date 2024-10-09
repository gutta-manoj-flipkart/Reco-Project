package com.flipkart.reco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.reco.listener.ConfigServiceDynamicListener;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.service.ConfigUpdateDao;
import com.flipkart.reco.model.DBEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class DBEntityController {
    @Autowired
    private ConfigUpdateRepository configUpdateRepository;
    List<ConfigServiceDynamicListener> listeners = new ArrayList<>();

    @PostMapping("/getdetails")
    public ResponseEntity<Object> ping(@RequestBody String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
        List<DBEntity> body = configUpdateDao.getEntriesBetweenTimestamps(new Timestamp(node.get("fromDateTime_UNIX").asLong()),new Timestamp(node.get("toDateTime_UNIX").asLong()));
        return ResponseEntity.ok(body);
    }
    @PostMapping("/client")
    public void client(@RequestBody String json) throws JsonProcessingException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        JsonNode bucketsNode = node.get("buckets");
        if (bucketsNode != null && bucketsNode.isArray()) {
            for (JsonNode bucketNode : bucketsNode) {
                String bucketName = bucketNode.asText();
                ConfigServiceDynamicListener configServiceDynamicListener = new ConfigServiceDynamicListener(bucketName, configUpdateRepository);
                System.out.println("Listener setup for bucket "+ bucketName);
                listeners.add(configServiceDynamicListener);
            }
        }
    }
}
