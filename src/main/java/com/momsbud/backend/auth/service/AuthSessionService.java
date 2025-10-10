package com.momsbud.backend.auth.service;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthSessionService {
    /** Revoke the session corresponding to the Bearer token in the request. */
    void logout(HttpServletRequest request);

    /** Revoke all sessions for the authenticated user (optional, if you want logout-all). */
    void logoutAll(String userId);
}
