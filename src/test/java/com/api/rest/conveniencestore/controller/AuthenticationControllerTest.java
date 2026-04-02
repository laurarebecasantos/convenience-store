package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.AuthenticationDto;
import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.service.TokenService;
import com.api.rest.conveniencestore.validations.PasswordValidator;
import com.api.rest.conveniencestore.validations.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private PasswordValidator passwordValidator;

    @MockBean
    private UserValidator userValidator;

    @Test
    void login_WhenValidCredentials_ShouldReturnToken() throws Exception {
        User user = new User(new UserDto("testuser", "Password1", "t@t.com", Roles.USER, Status.ACTIVE));
        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(tokenService.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthenticationDto("testuser", "Password1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void login_WhenInvalidCredentials_ShouldReturn401() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("bad credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthenticationDto("wronguser", "WrongPass1"))))
                .andExpect(status().isUnauthorized());
    }
}
