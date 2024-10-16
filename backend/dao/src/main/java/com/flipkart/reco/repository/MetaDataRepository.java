package com.flipkart.reco.repository;

import com.flipkart.reco.model.AppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaDataRepository extends JpaRepository<AppEntity, String> {
    @Query("SELECT e FROM AppEntity e WHERE e.zone LIKE %:zone%")
    List<AppEntity> findByZoneContaining(@Param("zone") String zone);
}
