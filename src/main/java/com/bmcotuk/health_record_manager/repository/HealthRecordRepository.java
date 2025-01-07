package com.bmcotuk.health_record_manager.repository;

import com.bmcotuk.health_record_manager.repository.model.HealthRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, String> {

    @Query("SELECT hr FROM HealthRecord hr " +
            "WHERE hr.code = :code ")
    Optional<HealthRecord> findByCode(String code);

    @Query("SELECT hr FROM HealthRecord hr ")
    List<HealthRecord> findAllPaginated(Pageable pageable);
}
