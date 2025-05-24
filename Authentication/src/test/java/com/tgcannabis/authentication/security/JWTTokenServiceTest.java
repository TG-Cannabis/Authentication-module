package com.tgcannabis.authentication.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JWTTokenServiceTest {
    private JWTTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JWTTokenService();
    }

    @Test
    void generateToken_shouldReturnValidJWT() {
        String email = "user@example.com";

        String token = jwtTokenService.generateToken(email);

        assertThat(token).isNotNull();
        assertThat(jwtTokenService.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtTokenService.generateToken("test@example.com");

        boolean isValid = jwtTokenService.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        String fakeToken = "eyJhbGciOiJIUzI1NiJ9.fake.payload.signature";

        boolean isValid = jwtTokenService.validateToken(fakeToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void getUserId_shouldExtractEmailFromValidToken() {
        String email = "extract@example.com";
        String token = jwtTokenService.generateToken(email);

        String extractedEmail = jwtTokenService.getUserId(token);

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    void getUserId_shouldThrowExceptionForInvalidToken() {
        String fakeToken = "invalid.token.structure";

        assertThatThrownBy(() -> jwtTokenService.getUserId(fakeToken))
                .isInstanceOf(JwtException.class);
    }
}
