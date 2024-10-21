package com.flipkart.reco.repository;

import com.flipkart.reco.model.DBEntityCompositeKey;
import com.flipkart.reco.model.DBEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ConfigUpdateRepository extends JpaRepository<DBEntity, DBEntityCompositeKey> {
    List<DBEntity> findByTimestampBetween(Timestamp from, Timestamp to);

    List<DBEntity> findByTimestampBetweenAndZones(Timestamp from, Timestamp to, String Zone);

    @Query("SELECT MAX(e.version) FROM DBEntity e WHERE e.name = :name")
    Integer findHighestVersionByName(@Param("name") String name);
}

