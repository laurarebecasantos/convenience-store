package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.AuthenticationDto;
import com.api.rest.conveniencestore.dto.TokenJWTDto;
import com.api.rest.conveniencestore.exceptions.AutheticationException;
import com.api.rest.conveniencestore.exceptions.PasswordValidateException;
import com.api.rest.conveniencestore.exceptions.UsernameValidateException;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.service.TokenService;
import com.api.rest.conveniencestore.utils.MessageConstants;
import com.api.rest.conveniencestore.validations.PasswordValidator;
import com.api.rest.conveniencestore.validations.UserValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private UserValidator userValidator;

    @PostMapping
    public ResponseEntity<TokenJWTDto> login(@Valid @RequestBody AuthenticationDto autDto) throws PasswordValidateException, AutheticationException, UsernameValidateException {
        userValidator.validateUsernameAuthetication(autDto.username());
        passwordValidator.validatePassword(autDto.password());

        var authenticationToken = new UsernamePasswordAuthenticationToken(autDto.username(), autDto.password());
        try {
            Authentication authentication = manager.authenticate(authenticationToken);
            String tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
            return ResponseEntity.ok(new TokenJWTDto(tokenJWT));
        } catch (Exception e) {
            throw new AutheticationException(MessageConstants.CREDENTIALS_INVALID);
        }
    }
}