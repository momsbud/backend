package com.momsbud.backend.tracking.repo;

import com.momsbud.backend.tracking.model.MetricType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetricTypeRepository extends JpaRepository<MetricType, String> {
    Optional<MetricType> findByCodeAndIsDeletedFalse(String code);
    List<MetricType> findAllByEnabledTrueAndIsDeletedFalseOrderByLabelAsc();
}
