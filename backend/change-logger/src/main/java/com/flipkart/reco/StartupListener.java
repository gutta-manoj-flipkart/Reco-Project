package com.flipkart.reco;

import com.flipkart.reco.controller.ChangeLoggerController;
import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.repository.MetaDataRepository;
import com.flipkart.reco.resource.ConfigServiceDynamicListener;
import com.flipkart.reco.service.ConfigChangeLoggerService;
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
        private ConfigChangeLoggerService configChangeLoggerService;
        @EventListener(ApplicationReadyEvent.class)
        public void onApplicationReady() {
            configChangeLoggerService.restartOps();
        }
    }
