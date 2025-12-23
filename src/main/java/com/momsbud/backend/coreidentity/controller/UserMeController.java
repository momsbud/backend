package com.momsbud.backend.coreidentity.controller;

import com.momsbud.backend.coreidentity.dto.UserMeResponse;
import com.momsbud.backend.coreidentity.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserMeController {

    private final UserQueryService userQueryService;

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        String userId = auth.getPrincipal().toString();
        return ResponseEntity.ok(userQueryService.getUserById(userId));
    }
}
