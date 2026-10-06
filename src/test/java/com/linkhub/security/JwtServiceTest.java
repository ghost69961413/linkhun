package com.linkhub.security;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    @Test
    void acceptsRawRenderStyleSecret() {
        String secret = "render-generated-secret-0123456789-abcdef";

        assertArrayEquals(secret.getBytes(StandardCharsets.UTF_8), JwtService.secretKeyBytes(secret));
    }

    @Test
    void acceptsBase64EncodedSecret() {
        byte[] expected = "a sufficiently long jwt secret key value".getBytes(StandardCharsets.UTF_8);
        String secret = Base64.getEncoder().encodeToString(expected);

        assertArrayEquals(expected, JwtService.secretKeyBytes(secret));
    }

    @Test
    void rejectsSecretsThatCannotSecureHs256() {
        assertThrows(IllegalStateException.class, () -> JwtService.secretKeyBytes("short"));
    }
}
