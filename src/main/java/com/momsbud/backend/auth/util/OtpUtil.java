package com.momsbud.backend.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class OtpUtil {
    private static final SecureRandom RND = new SecureRandom();
    private OtpUtil() {}

    public static String numericCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(RND.nextInt(10));
        return sb.toString();
    }

    /** SHA-256(code + pepper) hex */
    public static String hash(String code, String pepper) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(code.getBytes(StandardCharsets.UTF_8));
            if (pepper != null) md.update(pepper.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(md.digest());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash OTP", e);
        }
    }

    /** keep only digits; if 10 digits assume India and prefix 91 */
    public static String normalizePhone(String phoneRaw) {
        if (phoneRaw == null) return null;
        String p = phoneRaw.replaceAll("[^0-9]", "");
        if (p.length() == 10) p = "91" + p;
        return p;
    }
}
