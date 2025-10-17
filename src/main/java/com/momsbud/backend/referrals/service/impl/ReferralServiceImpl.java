package com.momsbud.backend.referrals.service.impl;

import com.momsbud.backend.referrals.dto.*;
import com.momsbud.backend.referrals.model.ReferralAttribution;
import com.momsbud.backend.referrals.model.ReferralCode;
import com.momsbud.backend.referrals.repo.ReferralAttributionRepository;
import com.momsbud.backend.referrals.repo.ReferralCodeRepository;
import com.momsbud.backend.referrals.service.ReferralService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferralServiceImpl implements ReferralService {

    private final ReferralCodeRepository codeRepo;
    private final ReferralAttributionRepository attrRepo;

    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray(); // no 0/O/I/1

    @Override
    public CodeResponse createCode(String doctorUserId, String requestedCode) {
        String code = (requestedCode == null || requestedCode.isBlank())
                ? generateCode(8)
                : normalize(requestedCode);

        if (codeRepo.existsByDoctorUserIdAndCodeAndIsDeletedFalse(doctorUserId, code)) {
            throw new IllegalArgumentException("Code already exists for this doctor.");
        }

        ReferralCode rc = ReferralCode.builder()
                .doctorUserId(doctorUserId)
                .code(code)
                .active(true)
                .build();
        rc = codeRepo.save(rc);

        long count = 0L;
        return CodeResponse.builder()
                .id(rc.getId())
                .code(rc.getCode())
                .active(rc.isActive())
                .createdAt(rc.getCreatedAt())
                .attributions(count)
                .build();
    }

    @Override
    public List<CodeResponse> listCodes(String doctorUserId) {
        return codeRepo.findAllByDoctorUserIdAndIsDeletedFalseOrderByCreatedAtDesc(doctorUserId)
                .stream()
                .map(rc -> CodeResponse.builder()
                        .id(rc.getId())
                        .code(rc.getCode())
                        .active(rc.isActive())
                        .createdAt(rc.getCreatedAt())
                        .attributions(attrRepo.countByCodeIdAndIsDeletedFalse(rc.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplyCodeResponse applyCode(String userId, String codeText) {
        String norm = normalize(codeText);
        ReferralCode rc = codeRepo.findByCodeAndActiveTrue(norm)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or inactive code"));

        return attrRepo.findByCodeIdAndUserIdAndIsDeletedFalse(rc.getId(), userId)
                .map(existing -> ApplyCodeResponse.builder()
                        .codeId(rc.getId())
                        .userId(userId)
                        .attributedAt(existing.getAttributedAt())
                        .created(false)
                        .build())
                .orElseGet(() -> {
                    ReferralAttribution ra = ReferralAttribution.builder()
                            .codeId(rc.getId())
                            .userId(userId)
                            .attributedAt(OffsetDateTime.now())
                            .build();
                    attrRepo.save(ra);
                    return ApplyCodeResponse.builder()
                            .codeId(rc.getId())
                            .userId(userId)
                            .attributedAt(ra.getAttributedAt())
                            .created(true)
                            .build();
                });
    }

    @Override
    public String exportCsv(String doctorUserId) {
        StringBuilder sb = new StringBuilder("code,total_attributions,created_at\n");
        codeRepo.findAllByDoctorUserIdAndIsDeletedFalseOrderByCreatedAtDesc(doctorUserId)
                .forEach(rc -> sb.append(rc.getCode()).append(",")
                        .append(attrRepo.countByCodeIdAndIsDeletedFalse(rc.getId())).append(",")
                        .append(rc.getCreatedAt()).append("\n"));
        return sb.toString();
    }

    /* helpers */
    private static String normalize(String in) {
        return in.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
    }
    private static String generateCode(int len) {
        char[] out = new char[len];
        for (int i=0;i<len;i++) out[i]=ALPHANUM[RNG.nextInt(ALPHANUM.length)];
        return new String(out);
    }
}
