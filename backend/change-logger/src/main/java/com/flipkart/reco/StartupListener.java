package com.flipkart.reco;

import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.repository.MetaDataRepository;
import com.flipkart.reco.resource.ConfigServiceDynamicListener;
import com.flipkart.reco.service.MetaDataDao;
import com.flipkart.reco.util.ConfigListenerStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
    public class StartupListener {
        @Autowired
        private MetaDataRepository metaDataRepository;
        @Autowired
        private ConfigUpdateRepository configUpdateRepository;
        @EventListener(ApplicationReadyEvent.class)
        public void onApplicationReady() {
            MetaDataDao metaDataDao = new MetaDataDao(metaDataRepository);
            List<AppEntity> data = metaDataDao.findByZone("gcp");
            for (AppEntity appEntity : data) {
                String name = appEntity.getName();
                String zone = appEntity.getZone();
                if(ConfigListenerStorageUtil.getConfigListenerStorageUtilInstance().get(name) == null) {
                    ConfigServiceDynamicListener listener = new ConfigServiceDynamicListener(name,zone,configUpdateRepository);
                    ConfigListenerStorageUtil.getConfigListenerStorageUtilInstance().add(name, listener);
                }
            }
        }
    }
