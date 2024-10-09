package com.reco.dao;

import com.reco.model.DBEntity;
import com.reco.repository.ConfigUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.List;

public class ConfigUpdateDAO {

    private final ConfigUpdateRepository configUpdateRepository;

    public ConfigUpdateDAO(ConfigUpdateRepository configUpdateRepository) {
        this.configUpdateRepository = configUpdateRepository;
    }

    public void addUpdate(DBEntity dbEntity) {
        configUpdateRepository.save(dbEntity);
    }
    public List<DBEntity> getEntriesBetweenTimestamps(Timestamp from, Timestamp to) {
        return configUpdateRepository.findByTimestampBetween(from, to);
    }
}
