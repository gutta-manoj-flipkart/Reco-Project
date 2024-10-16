package com.flipkart.reco.service;

import com.flipkart.reco.model.DBEntity;
import com.flipkart.reco.model.DBEntityDTO;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import org.hibernate.query.NativeQuery;
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

    public void addUpdate(DBEntity dbEntity) {
        configUpdateRepository.save(dbEntity);
    }
    public List<DBEntityDTO> getEntriesBetweenTimestamps(Timestamp from, Timestamp to) {
        List<DBEntity> data=configUpdateRepository.findByTimestampBetween(from, to);
        List<DBEntityDTO> result=new ArrayList<>();
        for(DBEntity dbEntity:data){
            DBEntityDTO db=new DBEntityDTO();
            db.setName(dbEntity.getName());
            db.setVersion(dbEntity.getVersion()); // Cast to Long and then convert to int
            db.setType(dbEntity.getType());
            db.setAuthor(dbEntity.getAuthor());
            db.setMsg(dbEntity.getMsg());
            db.setTimestamp(dbEntity.getTimestamp()); // Convert JSON timestamp to SQL timestamp
            db.setChangeData(new String(dbEntity.getChangeData()));
            db.setZones(dbEntity.getZones());
            result.add(db);
        }
        return result;
    }
    public Integer findHighestVersionByName(String name){
        return configUpdateRepository.findHighestVersionByName(name);
    }
    public void storetoDB(String name, String zone, String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object parsedJson = parser.parse(json);
        if (parsedJson instanceof JSONObject) {
            JSONObject obj = (JSONObject) parsedJson;
            DBEntity db = new DBEntity();
            db.setName(name);
            db.setVersion(((Long) obj.get("version")).intValue()); // Cast to Long and then convert to int
            db.setType(obj.get("type").toString());
            db.setAuthor(obj.get("author").toString());
            db.setMsg(obj.get("msg").toString());
            db.setTimestamp(new Timestamp((long) obj.get("ts"))); // Convert JSON timestamp to SQL timestamp
            db.setChangeData(("before: " + obj.get("before").toString() + " after: " + obj.get("after").toString()).getBytes());
            db.setZones(zone);
            configUpdateRepository.save(db);

        } else if (parsedJson instanceof JSONArray) {
            JSONArray arr = (JSONArray) parsedJson;
            DBEntity[] db = new DBEntity[arr.size()];
            for(int i = 0; i < arr.size(); i++) {
                JSONObject obj = (JSONObject)arr.get(i);
                db[i] = new DBEntity();
                db[i].setName(name);
                db[i].setVersion(((Long)obj.get("version")).intValue());
                db[i].setType(obj.get("type").toString());
                db[i].setAuthor(obj.get("author").toString());
                db[i].setMsg(obj.get("msg").toString());
                db[i].setTimestamp(new Timestamp((long)obj.get("ts")));
                db[i].setChangeData(("before: " + obj.get("before").toString() + " after : " + obj.get("after").toString()).getBytes());
                db[i].setZones(zone);
                configUpdateRepository.save(db[i]);
            }
        }
    }
}