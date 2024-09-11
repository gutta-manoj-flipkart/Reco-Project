package com.reco.dao;

import com.reco.model.DBEntity;
import com.reco.repository.DBEntityRepository;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;

public class DBEntityDAO {

    private final DBEntityRepository dbEntityRepository;

    public DBEntityDAO(DBEntityRepository dbEntityRepository) {
        this.dbEntityRepository = dbEntityRepository;
    }

    public void storetoDB(String json) throws ParseException {
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
            //dbEntityRepository.save(db[i]);
        }
        //dbEntityRepository.save(db[0]);
        //System.out.println(db[0]);
    }
}
