package com.flipkart.reco.controller;

import com.flipkart.reco.listener.ConfigServiceDynamicListener;
import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.repository.MetaDataRepository;
import com.flipkart.reco.service.ConfigUpdateDao;
import com.flipkart.reco.service.MetaDataDao;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/")
public class ChangeLoggerController {
    @Autowired
    private ConfigUpdateRepository configUpdateRepository;
    @Autowired
    private MetaDataRepository metaDataRepository;
    Map<String,ConfigServiceDynamicListener> listeners = new HashMap<>();
    @GetMapping("/test")
    public void test() throws ParseException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        System.out.println("test call a function");
        MetaDataDao metaDataDao = new MetaDataDao(metaDataRepository);
        List<AppEntity> data = metaDataDao.findByZone("gcp");
        for (AppEntity appEntity : data) {
            String name = appEntity.getName();
            String zone = appEntity.getZone();
            String url = "http://10.83.47.156/v1/history/";
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(url).queryParam("limit", "1");
            WebTarget target = webTarget.path(name);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.get();
            String json = response.readEntity(String.class);
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray) parser.parse(json);
            JSONObject obj = (JSONObject) arr.get(0);
            int version = ((Long) obj.get("version")).intValue();
            int db_version;
            ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
            Integer v = configUpdateDao.findHighestVersionByName(name);
            if(v == null)
                db_version = 0;
            else
                db_version = v;
            db_version++;
            while(db_version != version+1)
            {
                Client client1 = ClientBuilder.newClient();
                WebTarget webTarget1 = client1.target(url);
                WebTarget target1 = webTarget1.path(name).path(String.valueOf(db_version));
                Invocation.Builder invocationBuilder1 = target1.request(MediaType.APPLICATION_JSON);
                Response response1 = invocationBuilder1.get();
                String json1 = response1.readEntity(String.class);
                try {
                    configUpdateDao.storetoDB(name,zone,json1);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                db_version++;
            }
            if(!listeners.containsKey(name)){
                ConfigServiceDynamicListener listener = new ConfigServiceDynamicListener(name,zone,configUpdateRepository);
                listeners.put(name,listener);
            }
        }
    }
}
