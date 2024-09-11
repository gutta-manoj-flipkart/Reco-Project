package com.reco.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.reco.dao.DBEntityDAO;
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

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/configs")
public class DBEntityController {
    @Autowired
    private DBEntityRepository dbEntityRepository;
    @GetMapping
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong");
    }
    @PostMapping
    public ResponseEntity<?> ping(@RequestBody String json) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        // display the JsonNode
        System.out.println("dc: " + node.get("dc").asText());
        System.out.println("fdt: " + node.get("fromDateTime_UNIX").asText());
        System.out.println("Tdt: " + node.get("toDateTime_UNIX").asText());

        return ResponseEntity.ok("Successful");
    }
    @GetMapping("/populate")
    public ResponseEntity<?> populateDB() {
        String url
                = "http://10.83.47.156/v1/history/";

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url).queryParam("limit", "100");
        WebTarget target = webTarget.path("reco.griffin.intentRankingObjectives.prod");
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
