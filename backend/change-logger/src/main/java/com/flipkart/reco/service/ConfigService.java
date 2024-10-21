package com.flipkart.reco.service;

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

import java.util.Objects;

public class ConfigService {
    Client client = ClientBuilder.newClient();
    public void makeDBConsistent(String url, String name, String zone, ConfigUpdateDao configUpdateDao) throws ParseException {
        int version = getLatestVersion(url,name);
        int db_version;
        Integer v = configUpdateDao.findHighestVersionByName(name);
        db_version = Objects.requireNonNullElse(v, 0);
        db_version++;
        while(db_version != version+1)
        {
            String json1 = getUpdateByVersion(url,name,db_version);
            try {
                configUpdateDao.save(name,zone,json1);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            db_version++;
        }
    }

    public int getLatestVersion(String url, String configBucketName) throws ParseException {
        String json = getLatestUpdate(url,configBucketName);
        JSONParser parser = new JSONParser();
        JSONArray arr = (JSONArray) parser.parse(json);
        JSONObject obj = (JSONObject) arr.get(0);
        return ((Long) obj.get("version")).intValue();
    }
    public String getLatestUpdate(String url, String configBucketName){
        WebTarget target = client.target(url).queryParam("limit", "1").path(configBucketName);
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        return response.readEntity(String.class);
    }
    public String getUpdateByVersion(String url, String configBucketName,int version) {
        WebTarget target = client.target(url).path(configBucketName).path(String.valueOf(version));
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        return response.readEntity(String.class);
    }
}
