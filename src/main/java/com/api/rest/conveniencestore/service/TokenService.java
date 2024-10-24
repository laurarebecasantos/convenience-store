package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.utils.MessageConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("Convenience Store")
                    .withSubject(user.getUsername())
                    .withExpiresAt(expiration())
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException exception) {
            throw new JWTCreationException(MessageConstants.ERROR_JWT_TOKEN, exception);
        }
    }

    private Instant expiration() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }
}