package com.reco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.reco.dao.ConfigUpdateDAO;
import com.reco.dao.DBEntityDAO;
import com.reco.model.DBEntity;
import com.reco.repository.ConfigUpdateRepository;
import com.reco.repository.DBEntityRepository;
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

import java.sql.Timestamp;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/configs")
public class DBEntityController {
    @Autowired
    private DBEntityRepository dbEntityRepository;
    @Autowired
    private ConfigUpdateRepository configUpdateRepository;

    @PostMapping
    public ResponseEntity<Object> ping(@RequestBody String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        ConfigUpdateDAO configUpdateDAO = new ConfigUpdateDAO(configUpdateRepository);
        List<DBEntity> body = configUpdateDAO.getEntriesBetweenTimestamps(new Timestamp(node.get("fromDateTime_UNIX").asLong()),new Timestamp(node.get("toDateTime_UNIX").asLong()));
        return ResponseEntity.ok(body);
    }
    @PostMapping("/client")
    public void client(@RequestBody String json) throws JsonProcessingException {
        System.out.println(json);
    }
    @GetMapping("/populate")
    public ResponseEntity<?> populateDB() {
        String url
                = "http://10.83.47.156/v1/history/";

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url).queryParam("limit", "100");
        WebTarget target = webTarget.path("reco.pinbase.store.config");
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        String json = response.readEntity(String.class);
        DBEntityDAO DBEntityDAO = new DBEntityDAO(dbEntityRepository);
        try {
            DBEntityDAO.storetoDB(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Successful");
    }
}
