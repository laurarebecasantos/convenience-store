package com.api.rest.conveniencestore.controller;

import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.dto.UserListingDto;
import com.api.rest.conveniencestore.dto.UserUpdateDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.security.JwtAuthenticationFilter;
import com.api.rest.conveniencestore.service.TokenService;
import com.api.rest.conveniencestore.service.UserService;
import com.api.rest.conveniencestore.validations.PasswordValidator;
import com.api.rest.conveniencestore.validations.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordValidator passwordValidator;

    @MockBean
    private UserValidator userValidator;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(new UserDto("testuser", "Password1", "t@t.com", Roles.USER, Status.ACTIVE));
    }

    @Test
    void register_WhenValidData_ShouldReturn201() throws Exception {
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.registerUser(any())).thenReturn(user);

        UserDto dto = new UserDto("testuser", "Password1", "t@t.com", Roles.USER, Status.ACTIVE);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldReturn409() throws Exception {
        when(userService.existsByEmail("t@t.com")).thenReturn(true);

        UserDto dto = new UserDto("testuser", "Password1", "t@t.com", Roles.USER, Status.ACTIVE);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void list_WhenAuthenticated_ShouldReturn200() throws Exception {
        UserListingDto dto = new UserListingDto(user);
        when(userService.listUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser
    void list_WhenNoUsers_ShouldReturn404() throws Exception {
        when(userService.listUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void update_WhenUserExists_ShouldReturn200() throws Exception {
        when(userService.updateUser(eq(1L), any())).thenReturn(user);

        UserUpdateDto dto = new UserUpdateDto("newuser", "NewPass1", "new@t.com");

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_WhenAdminAndUserExists_ShouldReturn204() throws Exception {
        when(userService.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void status_WhenAdminSetsInactive_ShouldReturn200() throws Exception {
        when(userService.existsById(1L)).thenReturn(true);
        when(userService.updateUserStatus(eq(1L), eq(Status.INACTIVE))).thenReturn(user);

        mockMvc.perform(patch("/users/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "INACTIVE"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void roles_WhenAdminPromotes_ShouldReturn200() throws Exception {
        when(userService.existsById(1L)).thenReturn(true);
        when(userService.roleUserAdmin(eq(1L), eq(Roles.ADMIN))).thenReturn(user);

        mockMvc.perform(patch("/users/1/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("roles", "ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void list_WhenNotAuthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }
}
