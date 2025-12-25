package com.momsbud.backend.habits.repo;

import com.momsbud.backend.habits.model.UserHabitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserHabitLogRepository extends JpaRepository<UserHabitLog, String> {
    Optional<UserHabitLog> findByHabitIdAndUserIdAndLogDateAndIsDeletedFalse(String habitId, String userId, LocalDate logDate);
    List<UserHabitLog> findAllByUserIdAndLogDateAndIsDeletedFalse(String userId, LocalDate logDate);
    List<UserHabitLog> findAllByUserIdAndLogDateBetweenAndIsDeletedFalse(String userId, LocalDate start, LocalDate end);
}
