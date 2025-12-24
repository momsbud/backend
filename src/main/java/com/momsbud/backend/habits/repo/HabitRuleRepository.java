package com.momsbud.backend.habits.repo;

import com.momsbud.backend.habits.model.HabitRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface HabitRuleRepository extends JpaRepository<HabitRule, String> {

    @Query(value = """
        select * from habit_rule r
        where r.is_deleted = false
          and r.active = true
          and r.lob = :lob
          and (r.valid_from is null or r.valid_from <= :now)
          and (r.valid_to is null or r.valid_to >= :now)
        order by r.priority desc, r.created_at desc
        """, nativeQuery = true)
    List<HabitRule> findActiveForLobNow(String lob, OffsetDateTime now);
}
