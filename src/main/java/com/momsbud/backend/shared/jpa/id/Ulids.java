package com.momsbud.backend.shared.jpa.id;

import java.security.SecureRandom;

/** Minimal ULID (time-part + randomness) → 26-char Crockford Base32. */
public final class Ulids {
    private static final char[] ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final SecureRandom RNG = new SecureRandom();

    private Ulids() {}

    /** ULID = 48-bit time (ms) + 80-bit randomness → 26 chars. */
    public static String newUlid() {
        long time = System.currentTimeMillis();        // 48 bits
        byte[] rand = new byte[10];                    // 80 bits
        RNG.nextBytes(rand);
        return encode(time, rand);
    }

    private static String encode(long time, byte[] rand) {
        // 26 chars = 130 bits (we’ll fill 128 and the top 2 zero)
        char[] out = new char[26];

        // time: 48 bits → 10 chars (5 bits/char; 10*5 = 50, top 2 zero)
        long t = time;
        for (int i = 9; i >= 0; i--) {
            out[i] = ALPHABET[(int) (t & 31)];
            t >>>= 5;
        }

        // randomness: 80 bits → 16 chars
        // pack bytes into a 80-bit number split in two longs
        long rHi = ((rand[0] & 0xFFL) << 32)
                | ((rand[1] & 0xFFL) << 24)
                | ((rand[2] & 0xFFL) << 16)
                | ((rand[3] & 0xFFL) << 8)
                | (rand[4] & 0xFFL);
        long rLo = ((rand[5] & 0xFFL) << 32)
                | ((rand[6] & 0xFFL) << 24)
                | ((rand[7] & 0xFFL) << 16)
                | ((rand[8] & 0xFFL) << 8)
                | (rand[9] & 0xFFL);

        // write 8 chars from rHi (40 bits)
        for (int i = 15; i >= 8; i--) {
            out[i] = ALPHABET[(int) (rHi & 31)];
            rHi >>>= 5;
        }
        // write 8 chars from rLo (40 bits)
        for (int i = 25; i >= 16; i--) {
            out[i] = ALPHABET[(int) (rLo & 31)];
            rLo >>>= 5;
        }
        return new String(out);
    }
}
