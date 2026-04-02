package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.model.User;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private User user;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret-key-123456789");
        user = new User(new UserDto("testuser", "Password1", "test@email.com", Roles.USER, Status.ACTIVE));
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = tokenService.generateToken(user);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void getSubject_ShouldReturnUsername() {
        String token = tokenService.generateToken(user);

        String subject = tokenService.getSubject(token);

        assertThat(subject).isEqualTo("testuser");
    }

    @Test
    void getSubject_WhenTokenInvalid_ShouldThrowJWTVerificationException() {
        assertThatThrownBy(() -> tokenService.getSubject("invalid.token.here"))
                .isInstanceOf(JWTVerificationException.class);
    }

    @Test
    void getSubject_WhenTokenSignedWithWrongSecret_ShouldThrow() {
        TokenService otherService = new TokenService();
        ReflectionTestUtils.setField(otherService, "secret", "other-secret");
        String tokenFromOtherSecret = otherService.generateToken(user);

        assertThatThrownBy(() -> tokenService.getSubject(tokenFromOtherSecret))
                .isInstanceOf(JWTVerificationException.class);
    }
}
