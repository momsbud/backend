package com.momsbud.backend.habits.repo;

import com.momsbud.backend.habits.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, String> {
    List<Habit> findAllByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId);
    Optional<Habit> findByIdAndUserIdAndIsDeletedFalse(String id, String userId);
}
