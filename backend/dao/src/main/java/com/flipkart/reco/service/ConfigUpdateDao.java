package com.flipkart.reco.service;

import com.flipkart.reco.model.DBEntity;
import com.flipkart.reco.model.DBEntityDTO;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConfigUpdateDao {

    private final ConfigUpdateRepository configUpdateRepository;

    public ConfigUpdateDao(ConfigUpdateRepository configUpdateRepository) {
        this.configUpdateRepository = configUpdateRepository;
    }

    public void addUpdate(JSONObject obj, String name, String zone) {
        DBEntity db = new DBEntity(name,
                new Timestamp((long) obj.get("ts")),
                zone,
                ((Long) obj.get("version")).intValue(),
                obj.get("type").toString(),
                obj.get("author").toString(),
                obj.get("msg").toString(),
                ("before: " + obj.get("before").toString() + " after: " + obj.get("after").toString()).getBytes());
        configUpdateRepository.save(db);
    }

    public Integer findHighestVersionByName(String name){
        return configUpdateRepository.findHighestVersionByName(name);
    }

    public List<DBEntityDTO> getEntriesBetweenTimestampsAndZones(Timestamp from, Timestamp to,String zone){
        List<DBEntity> data;
        if(zone.equalsIgnoreCase("all"))
            data=configUpdateRepository.findByTimestampBetween(from, to);
        else
            data=configUpdateRepository.findByTimestampBetweenAndZones(from, to, zone);
        return DBtoDTO(data);
    }

    public void save(String name, String zone, String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object parsedJson = parser.parse(json);
        if (parsedJson instanceof JSONObject obj) {
            addUpdate(obj, name, zone);

        } else if (parsedJson instanceof JSONArray arr) {
            for (Object o : arr) {
                JSONObject obj = (JSONObject) o;
                addUpdate(obj, name, zone);
            }
        }
    }

    private List<DBEntityDTO> DBtoDTO(List<DBEntity> data) {
        List<DBEntityDTO> result=new ArrayList<>();
        for(DBEntity dbEntity:data){
            DBEntityDTO db=new DBEntityDTO(
                    dbEntity.getName(),
                    dbEntity.getTimestamp(),
                    dbEntity.getZones(),
                    dbEntity.getVersion(),
                    dbEntity.getType(),
                    dbEntity.getAuthor(),
                    dbEntity.getMsg(),
                    new String(dbEntity.getChangeData())
            );
            result.add(db);
        }
        return result;
    }
}