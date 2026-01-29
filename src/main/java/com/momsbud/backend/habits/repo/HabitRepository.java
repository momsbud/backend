package com.momsbud.backend.habits.repo;

import com.momsbud.backend.habits.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, String> {
    List<Habit> findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId);
    Optional<Habit> findByIdAndUserIdAndIsDeletedFalse(String id, String userId);
    Optional<Habit> findByIdAndIsDeletedFalse(String id);
    @Query(value = """
        select * from habit h
        where h.is_deleted = false
          and h.active = true
          and :tag = any(h.tags)
        """, nativeQuery = true)
    List<Habit> findActiveByTag(String tag);
    List<Habit> findAllByIdInAndIsDeletedFalse(Collection<String> ids);

}
