package com.momsbud.backend.tracking.repo;

import com.momsbud.backend.tracking.model.UserMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface UserMetricRepository extends JpaRepository<UserMetric, String> {

    List<UserMetric> findAllByUserIdAndRecordedAtBetweenAndIsDeletedFalseOrderByRecordedAtDesc(
            String userId, OffsetDateTime start, OffsetDateTime end);

    List<UserMetric> findAllByUserIdAndTypeCodeAndIsDeletedFalseOrderByRecordedAtDesc(
            String userId, String typeCode);

    Optional<UserMetric> findFirstByUserIdAndTypeCodeAndIsDeletedFalseOrderByRecordedAtDesc(
            String userId, String typeCode);
}
