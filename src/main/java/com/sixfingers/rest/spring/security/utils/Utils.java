package com.sixfingers.rest.spring.security.utils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public final class Utils {

    private Utils(){
    }

    public static final String X_SESSION_ID = "X-SESSION-ID";

    public static String generateRandomHash() {
        UUID uuid = UUID.randomUUID();
        // Create byte[] for base64 from uuid
        byte[] src = ByteBuffer.wrap(new byte[16])
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .array();
        // Encode to Base64 and remove trailing ==
        return Base64.getUrlEncoder().encodeToString(src).substring(0, 22);
    }

}
