package com.flipkart.reco.controller;

import com.flipkart.reco.util.ConfigListenerStorageUtil;
import com.flipkart.reco.resource.ConfigServiceDynamicListener;
import com.flipkart.reco.model.AppEntity;
import com.flipkart.reco.repository.ConfigUpdateRepository;
import com.flipkart.reco.repository.MetaDataRepository;
import com.flipkart.reco.service.MetaDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/")
public class ChangeLoggerController {
    @Autowired
    private ConfigUpdateRepository configUpdateRepository;
    @Autowired
    private MetaDataRepository metaDataRepository;

    @GetMapping("/test")
    public void test() {
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
