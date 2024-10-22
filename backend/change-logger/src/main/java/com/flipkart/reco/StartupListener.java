package com.flipkart.reco;

import com.flipkart.reco.service.ConfigChangeLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

@Component
    public class StartupListener {
        @Autowired
        private ConfigChangeLoggerService configChangeLoggerService;
        @EventListener(ApplicationReadyEvent.class)
        public void onApplicationReady() {
            configChangeLoggerService.restartOps();
        }
    }
