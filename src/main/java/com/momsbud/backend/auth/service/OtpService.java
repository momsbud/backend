package com.momsbud.backend.auth.service;

import com.momsbud.backend.auth.dto.OtpSendRequest;
import com.momsbud.backend.auth.dto.OtpSendResponse;
import com.momsbud.backend.auth.dto.OtpVerifyRequest;
import com.momsbud.backend.auth.dto.OtpVerifyResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface OtpService {
    OtpSendResponse send(OtpSendRequest req, HttpServletRequest http);
    OtpVerifyResponse verify(OtpVerifyRequest req, HttpServletRequest http);
}
