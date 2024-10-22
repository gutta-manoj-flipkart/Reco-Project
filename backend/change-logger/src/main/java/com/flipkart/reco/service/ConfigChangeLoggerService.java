package com.flipkart.reco.service;

import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.repository.ConfigStorage;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.repository.MetaDataRepository;
import com.flipkart.reco.resource.ConfigServiceDynamicListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ConfigChangeLoggerService {
    private ConfigUpdateRepository configUpdateRepository;
    private MetaDataRepository metaDataRepository;
    @Autowired
    public ConfigChangeLoggerService(ConfigUpdateRepository configUpdateRepository, MetaDataRepository metaDataRepository) {
        this.configUpdateRepository = configUpdateRepository;
        this.metaDataRepository = metaDataRepository;
    }
    public void restartOps()
    {
        MetaDataDao metaDataDao = new MetaDataDao(metaDataRepository);
        List<AppEntity> data = metaDataDao.findByZone(()-> {
            if (System.getenv("FCP_ZONE").equalsIgnoreCase("asia-south1")) {
                return "gcp";
            } else if (System.getenv("FCP_ZONE").equalsIgnoreCase("in-hyderabad-1")) {
                return "hyd";
            } else {
                return "ch";
            }
        });
        for (AppEntity appEntity : data) {
            String name = appEntity.getName();
            String zone = appEntity.getZone();
            if (ConfigStorage.getConfigStorageInstance().get(name) == null) {
                ConfigServiceDynamicListener listener = new ConfigServiceDynamicListener(name, zone, configUpdateRepository);
                ConfigStorage.getConfigStorageInstance().add(name, listener);
            }
        }
    }
}
