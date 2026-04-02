package com.api.rest.conveniencestore.service;

import com.api.rest.conveniencestore.dto.UserDto;
import com.api.rest.conveniencestore.dto.UserListingDto;
import com.api.rest.conveniencestore.dto.UserUpdateDto;
import com.api.rest.conveniencestore.enums.Roles;
import com.api.rest.conveniencestore.enums.Status;
import com.api.rest.conveniencestore.exceptions.UserNotFoundException;
import com.api.rest.conveniencestore.model.User;
import com.api.rest.conveniencestore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // UserDto(username, password, email, role, status)
    private static final UserDto USER_DTO =
            new UserDto("testuser", "Password1", "test@email.com", Roles.USER, Status.ACTIVE);

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USER_DTO);
    }

    @Test
    void registerUser_ShouldEncryptPasswordAndSave() {
        when(passwordEncoder.encode("Password1")).thenReturn("encrypted");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(USER_DTO);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("Password1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void listUsers_ShouldReturnOnlyActiveUsers() {
        when(userRepository.findByStatus(Status.ACTIVE)).thenReturn(List.of(user));

        List<UserListingDto> result = userService.listUsers();

        assertThat(result).hasSize(1);
        verify(userRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void listUsers_WhenNoActiveUsers_ShouldReturnEmptyList() {
        when(userRepository.findByStatus(Status.ACTIVE)).thenReturn(List.of());

        List<UserListingDto> result = userService.listUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateAndSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncrypted");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, new UserUpdateDto("newuser", "NewPass1", "new@email.com"));

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, new UserUpdateDto("u", "Pass1A", "e@e.com")))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteUser_ShouldCallDelete() {
        when(userRepository.getReferenceById(1L)).thenReturn(user);

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void updateUserStatus_ToInactive_ShouldSetStatusAndSave() {
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserStatus(1L, Status.INACTIVE);

        assertThat(result.getStatus()).isEqualTo(Status.INACTIVE);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserStatus_ToActive_ShouldSetStatusAndSave() {
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserStatus(1L, Status.ACTIVE);

        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void roleUserAdmin_ShouldSetRoleAdminAndSave() {
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.roleUserAdmin(1L, Roles.ADMIN);

        assertThat(result.getRole()).isEqualTo(Roles.ADMIN);
        verify(userRepository).save(user);
    }

    @Test
    void existsByUsername_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThat(userService.existsByUsername("testuser")).isTrue();
    }

    @Test
    void existsByEmail_WhenNotExists_ShouldReturnFalse() {
        when(userRepository.existsByEmail("x@x.com")).thenReturn(false);

        assertThat(userService.existsByEmail("x@x.com")).isFalse();
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertThat(userService.existsById(1L)).isTrue();
    }
}
