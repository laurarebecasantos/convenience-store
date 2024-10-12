package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.AuthenticationDto;
import com.api.rest.conveniencestore.exceptions.AutheticationInvalidException;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping
    public ResponseEntity<String> login(@Valid @RequestBody AuthenticationDto autDto) throws AutheticationInvalidException {
        var token = new UsernamePasswordAuthenticationToken(autDto.username(), autDto.password());
        try {
            Authentication authentication = manager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok(tokenService.generateToken((User) authentication.getPrincipal()));
        } catch (Exception e) {
            throw new AutheticationInvalidException("Credentials: Invalid username or password.");
        }
    }
}
