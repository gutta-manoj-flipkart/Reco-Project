package com.flipkart.reco.service;

import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.repository.MetaDataRepository;

import java.util.List;

public class MetaDataDao {
    private final MetaDataRepository metaDataRepository;
    public MetaDataDao(MetaDataRepository metaDataRepository) {
        this.metaDataRepository = metaDataRepository;
    }
    public void addUpdate(AppEntity appEntity) {
        metaDataRepository.save(appEntity);
    }
    public List<AppEntity> findByZone(String zone) {
        if(zone.equalsIgnoreCase("all"))
            return metaDataRepository.findAll();
        else
            return metaDataRepository.findByZoneContaining(zone);
    }
}
