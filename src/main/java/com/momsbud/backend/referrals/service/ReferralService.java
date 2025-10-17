package com.momsbud.backend.referrals.service;

import com.momsbud.backend.referrals.dto.*;

import java.util.List;

public interface ReferralService {
    CodeResponse createCode(String doctorUserId, String requestedCode);
    List<CodeResponse> listCodes(String doctorUserId);
    ApplyCodeResponse applyCode(String userId, String codeText);
    String exportCsv(String doctorUserId);
}
