package com.reco.repository;

import com.reco.model.CompositeKey;
import com.reco.model.DBEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DBEntityRepository extends JpaRepository<DBEntity, CompositeKey> {
}
