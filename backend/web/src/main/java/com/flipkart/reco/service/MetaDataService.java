package com.flipkart.reco.service;

import com.flipkart.reco.model.AppData;
import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.repository.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class MetaDataService {
    private MetaDataRepository metaDataRepository;
    @Autowired
    public MetaDataService(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }
    public void addClient(AppData appData) {
        MetaDataDao metaDataDao = new MetaDataDao(metaDataRepository);
        Map<String, String> buckets = appData.getBuckets();
        for (Map.Entry<String, String> entry : buckets.entrySet()) {
            metaDataDao.addUpdate(new AppEntity(appData.getAppid(), "bucket", entry.getKey(), entry.getValue()));
        }
    }
}
