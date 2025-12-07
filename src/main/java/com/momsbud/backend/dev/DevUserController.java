package com.momsbud.backend.dev;

import com.momsbud.backend.coreidentity.entity.User;
import com.momsbud.backend.coreidentity.entity.UserSession;
import com.momsbud.backend.coreidentity.enums.UserType;
import com.momsbud.backend.coreidentity.repo.UserRepository;
import com.momsbud.backend.coreidentity.repo.UserSessionRepository;
import com.momsbud.backend.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/dev")
     // IMPORTANT → loads only for "dev" Spring profile
public class DevUserController {

    private final UserRepository userRepo;
    private final UserSessionRepository sessionRepo;
    private final JwtService jwtService;

    public DevUserController(UserRepository userRepo,
                             UserSessionRepository sessionRepo,
                             JwtService jwtService) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.jwtService = jwtService;
    }

    @PostMapping("/switch-user-type")
    public String switchUserType(@RequestParam String userId,
                                 @RequestParam UserType userType) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setUserType(userType);
        userRepo.save(user);

        // Create new session row for the token
        String newJti = "DEV-" + Instant.now().toEpochMilli();

        UserSession session = new UserSession();
        session.setId(newJti);
        session.setUserId(userId);
        session.setActive(true);
        session.setCreatedAt(Instant.now());
        session.setUpdatedAt(Instant.now());
        sessionRepo.save(session);

        // Generate new JWT with updated userType
        String jwt = jwtService.createToken(userId, newJti);

        return """
                {
                  "message": "User type updated successfully",
                  "userId": "%s",
                  "newUserType": "%s",
                  "jwt": "%s"
                }
                """.formatted(userId, userType.name(), jwt);
    }
}
