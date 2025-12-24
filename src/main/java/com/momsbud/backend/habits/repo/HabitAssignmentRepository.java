package com.momsbud.backend.habits.repo;

import com.momsbud.backend.habits.model.HabitAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface HabitAssignmentRepository extends JpaRepository<HabitAssignmentEntity, String> {

    Optional<HabitAssignmentEntity> findFirstByUserIdAndHabitIdAndStatusAndIsDeletedFalse(
            String userId, String habitId, String status
    );

    Optional<HabitAssignmentEntity> findFirstByUserIdAndDedupeKeyAndStatusAndIsDeletedFalse(
            String userId, String dedupeKey, String status
    );
    @Query(value = """
        select * from habit_assignment ha
        where ha.is_deleted = false
          and ha.user_id = :userId
          and ha.status = 'ACTIVE'
        order by ha.assigned_from desc
        """, nativeQuery = true)
    List<HabitAssignmentEntity> findActiveByUserId(String userId);
    long countByUserIdAndStatusAndIsDeletedFalse(String userId, String status);

    @Modifying
    @Transactional
    @Query(value = """
        update habit_assignment
        set status = 'ENDED',
            updated_at = :now
        where is_deleted = false
          and status = 'ACTIVE'
          and assigned_until is not null
          and assigned_until < :now
        """, nativeQuery = true)
    int endExpiredAssignments(OffsetDateTime now);

    @Query(value = """
    select * from habit_assignment ha
    where ha.is_deleted = false
      and ha.user_id = :userId
      and ha.status = 'ACTIVE'
      and ha.rule_id is not null
""", nativeQuery = true)
    List<HabitAssignmentEntity> findActiveRuleAssignments(String userId);
}
