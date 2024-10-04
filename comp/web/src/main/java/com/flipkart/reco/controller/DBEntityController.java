package com.flipkart.reco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.reco.listener.ConfigServiceDynamicListener;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.service.ConfigUpdateDao;
import com.flipkart.reco.model.DBEntity;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class DBEntityController {
    @Autowired
    private ConfigUpdateRepository configUpdateRepository;

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
        ConfigServiceDynamicListener configServiceDynamicListener = new ConfigServiceDynamicListener(node.get("bucket").asText(),configUpdateRepository);
    }
    @GetMapping("/populate")
    public ResponseEntity<?> populateDB() {
        String url
                = "http://10.83.47.156/v1/history/";

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url);
        WebTarget target = webTarget.path("reco.griffin.contentProvider.config").path(String.valueOf(2));
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        String json = response.readEntity(String.class);
        ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
        try {
            configUpdateDao.storetoDB("reco.griffin.contentProvider.config",json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Successful");
    }
}
