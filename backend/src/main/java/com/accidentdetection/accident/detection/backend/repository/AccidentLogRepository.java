package com.accidentdetection.accident.detection.backend.repository;

import com.accidentdetection.accident.detection.backend.entity.AccidentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccidentLogRepository extends JpaRepository<AccidentLog, Long> {
    List<AccidentLog> findByUserUserIdOrderByTimestampDesc(Long userId);
}

