package com.momsbud.backend.habits.repo;

import com.momsbud.backend.habits.model.HabitAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
