package com.flipkart.reco.service;

import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.model.ZoneProvider;
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
    public List<AppEntity> findByZone(ZoneProvider zoneProvider) {
        String zone = zoneProvider.getZone();
        return metaDataRepository.findByZoneContaining(zone);
    }
}
