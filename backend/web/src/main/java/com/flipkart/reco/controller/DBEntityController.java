package com.flipkart.reco.controller;

import com.flipkart.reco.model.*;
import com.flipkart.reco.service.ConfigService;
import com.flipkart.reco.service.MetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/")
public class DBEntityController {
    @Autowired
    private ConfigService configService;
    @Autowired
    private MetaDataService metaDataService;

    private final RestTemplate restTemplate;
    @Autowired
    public DBEntityController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @PostMapping("/getdetails")
    public ResponseEntity<Object> getDetails(@RequestBody ConfigFilterData data) {
        List<DBEntityDTO> body = configService.getDetails(data);
        return ResponseEntity.ok(body);
    }
    @PostMapping("/client")
    public String client(@RequestBody AppData appData) {
        metaDataService.addClient(appData);
        String url = "http://localhost:8081/restart";
        restTemplate.getForEntity(url, String.class);
        return "Success";
    }
}
