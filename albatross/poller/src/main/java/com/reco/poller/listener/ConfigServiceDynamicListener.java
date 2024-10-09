package com.reco.poller.listener;

import com.flipkart.kloud.config.BaseBucketUpdateListener;
import com.flipkart.kloud.config.Bucket;
import com.flipkart.kloud.config.DynamicBucket;
import com.flipkart.kloud.config.error.ConfigServiceException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

import com.reco.dao.ConfigUpdateDAO;
import com.reco.dao.DBEntityDAO;
import com.reco.model.DBEntity;
import com.reco.poller.util.ConfigServiceUtil;
import com.reco.repository.ConfigUpdateRepository;
import com.reco.repository.DBEntityRepository;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigServiceDynamicListener {
    @Autowired
    private DBEntityRepository dbEntityRepository;
    public ConfigServiceDynamicListener(String configBucketName,ConfigUpdateRepository configUpdateRepository) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ConfigUpdateDAO configUpdateDAO = new ConfigUpdateDAO(configUpdateRepository);
        try {
            DynamicBucket bucket = ConfigServiceUtil.getInstance()
                    .getConfigServiceDynamicBucket(configBucketName);
            int version = bucket.getMetadata().getVersion();
            int db_version = configUpdateRepository.findHighestVersionByName(configBucketName);
            while(version != db_version)
            {
                String url
                        = "http://10.83.47.156/v1/history/";

                Client client = ClientBuilder.newClient();
                WebTarget webTarget = client.target(url);
                WebTarget target = webTarget.path(configBucketName).path(String.valueOf(db_version+1));
                Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
                Response response = invocationBuilder.get();
                String json = response.readEntity(String.class);
                DBEntityDAO DBEntityDAO = new DBEntityDAO(dbEntityRepository);
                try {
                    DBEntityDAO.storetoDB(json);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                db_version = db_version + 1;
            }
            bucket.addListener(new BaseBucketUpdateListener() {
                @SneakyThrows
                @Override
                public void updated(Bucket oldBucket, Bucket newBucket) {
                    Timestamp timestamp = new Timestamp(bucket.getMetadata().getLastUpdated());
                    String zones = "someZone"; // Extract from metadata if applicable
                    int version = bucket.getMetadata().getVersion();
                    String type = "someType"; // Use relevant type
                    String author = "author"; // Or fetch dynamically
                    String msg = "Bucket updated";
                    String changeData = newBucket.getBucketJSON();

                    // Create the DBEntity object
                    DBEntity dbEntity = new DBEntity();
                    dbEntity.setName(configBucketName);
                    dbEntity.setZones(zones);
                    dbEntity.setVersion(version);
                    dbEntity.setType(type);
                    dbEntity.setAuthor(author);
                    dbEntity.setMsg(msg);
                    dbEntity.setChangeData(changeData);
                    dbEntity.setTimestamp(timestamp);
                    // Call configUpdateDAO.addUpdate with the DBEntity object
                    configUpdateDAO.addUpdate(dbEntity);
                }
            });

        }  catch (ConfigServiceException e) {
            log.error("Error while parsing config {} ", configBucketName, e);
        }
    }
}


