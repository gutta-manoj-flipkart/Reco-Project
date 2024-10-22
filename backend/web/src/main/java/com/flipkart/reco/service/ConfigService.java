package com.flipkart.reco.service;

import com.flipkart.reco.model.ConfigFilterData;
import com.flipkart.reco.model.DBEntityDTO;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
@Service
public class ConfigService {
    private ConfigUpdateRepository configUpdateRepository;
    @Autowired
    public ConfigService(ConfigUpdateRepository configUpdateRepository) {
        this.configUpdateRepository = configUpdateRepository;
    }
    public List<DBEntityDTO> getDetails(ConfigFilterData data)
    {
        ConfigUpdateDao configUpdateDao = new ConfigUpdateDao(configUpdateRepository);
        return configUpdateDao.getEntriesBetweenTimestampsAndZones(new Timestamp(Long.parseLong(data.getFromDateTime_UNIX())),new Timestamp((Long.parseLong(data.getToDateTime_UNIX()))), data.getDc());
    }

}
