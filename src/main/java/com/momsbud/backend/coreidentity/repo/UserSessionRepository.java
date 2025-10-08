package com.momsbud.backend.coreidentity.repo;

import com.momsbud.backend.coreidentity.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
}
