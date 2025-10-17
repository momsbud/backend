package com.momsbud.backend.referrals.controller;

import com.momsbud.backend.referrals.dto.*;
import com.momsbud.backend.referrals.service.ReferralService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService service; // <-- interface

    @PostMapping("/codes")
    public CodeResponse createCode(@RequestBody(required = false) CreateCodeRequest req, Authentication auth) {
        String doctorId = (String) auth.getPrincipal();
        return service.createCode(doctorId, req != null ? req.getCode() : null);
    }

    @GetMapping("/codes")
    public List<CodeResponse> listCodes(Authentication auth) {
        String doctorId = (String) auth.getPrincipal();
        return service.listCodes(doctorId);
    }

    @GetMapping(value = "/export.csv", produces = "text/csv")
    public void export(Authentication auth, HttpServletResponse resp) throws IOException {
        String doctorId = (String) auth.getPrincipal();
        String csv = service.exportCsv(doctorId);
        resp.setContentType(MediaType.TEXT_PLAIN_VALUE);
        resp.setHeader("Content-Disposition", "attachment; filename=\"referrals.csv\"");
        resp.getOutputStream().write(csv.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/apply")
    public ApplyCodeResponse apply(@RequestBody ApplyCodeRequest req, Authentication auth) {
        String userId = (String) auth.getPrincipal();
        return service.applyCode(userId, req.getCode());
    }
}
