package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.models.DBEntity;
import org.example.repositories.DBEntityRespository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/configs")
public class DBEntityController {
    @Autowired
    DBEntityRespository dbEntityRespository;
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
    public ResponseEntity<?> populateDB() throws ParseException {
        String url
                = "http://10.83.47.156/v1/history/";

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url).queryParam("limit", "100");
        WebTarget target = webTarget.path("reco.griffin.intentRankingObjectives.prod");
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        String json = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONArray arr = (JSONArray)parser.parse(json);
        int byt = 0;
        DBEntity[] db = new DBEntity[arr.size()];
        for(int i = 0; i < arr.size(); i++) {
            JSONObject obj = (JSONObject)arr.get(i);
            db[i] = new DBEntity();
            db[i].setName("reco.griffin.intentRankingObjectives.prod");
            db[i].setVersion(obj.get("version").toString());
            db[i].setType(obj.get("type").toString());
            db[i].setAuthor(obj.get("author").toString());
            db[i].setMsg(obj.get("msg").toString());
            db[i].setTimestamp(new Timestamp((long)obj.get("ts")));
            db[i].setChangeData("before: " + obj.get("before").toString() + " after : " + obj.get("after").toString());
            db[i].setZones("ALL");
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(db[i]);
                oos.flush();
                byt += bos.toByteArray().length;
                System.out.println(bos.toByteArray().length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Final: "+ byt);
            //System.out.println(db[i].getChangeData().getBytes().length);
            //dbEntityRespository.save(db[i]);
        }
        //System.out.println(db[0]);
        return ResponseEntity.ok("Successful");
    }
}
