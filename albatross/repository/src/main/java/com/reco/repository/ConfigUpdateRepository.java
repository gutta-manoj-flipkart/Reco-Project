package com.reco.repository;

import com.reco.model.CompositeKey;
import com.reco.model.DBEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ConfigUpdateRepository extends JpaRepository<DBEntity, CompositeKey> {
    List<DBEntity> findByTimestampBetween(Timestamp from, Timestamp to);
    @Query("SELECT MAX(DBEntity.version) FROM DBEntity WHERE DBEntity.name = :name")
    Integer findHighestVersionByName(@Param("name") String name);
}
