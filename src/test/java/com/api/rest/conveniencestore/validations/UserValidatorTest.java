package com.api.rest.conveniencestore.validations;

import com.api.rest.conveniencestore.exceptions.NameValidateException;
import com.api.rest.conveniencestore.exceptions.UsernameValidateException;
import com.api.rest.conveniencestore.service.ClientService;
import com.api.rest.conveniencestore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserService userService;

    @Mock
    private ClientService clientService;

    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator(userService, clientService);
    }

    // --- validateUsername ---

    @Test
    void validateUsername_WhenValid_ShouldNotThrow() {
        when(userService.existsByUsername("joao1")).thenReturn(false);

        assertThatCode(() -> userValidator.validateUsername("joao1"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUsername_WhenEmpty_ShouldThrow() {
        assertThatThrownBy(() -> userValidator.validateUsername(""))
                .isInstanceOf(UsernameValidateException.class)
                .hasMessageContaining("vazio");
    }

    @Test
    void validateUsername_WhenTooShort_ShouldThrow() {
        assertThatThrownBy(() -> userValidator.validateUsername("ab"))
                .isInstanceOf(UsernameValidateException.class)
                .hasMessageContaining("3");
    }

    @Test
    void validateUsername_WhenInvalidPattern_ShouldThrow() {
        assertThatThrownBy(() -> userValidator.validateUsername("jo@ao"))
                .isInstanceOf(UsernameValidateException.class);
    }

    @Test
    void validateUsername_WhenAlreadyExists_ShouldThrow() {
        when(userService.existsByUsername("existingUser")).thenReturn(true);

        assertThatThrownBy(() -> userValidator.validateUsername("existingUser"))
                .isInstanceOf(UsernameValidateException.class)
                .hasMessageContaining("cadastrado");
    }

    // --- validateUsernameAuthetication ---

    @Test
    void validateUsernameAuthetication_WhenValid_ShouldNotThrow() {
        assertThatCode(() -> userValidator.validateUsernameAuthetication("joao1"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUsernameAuthetication_WhenEmpty_ShouldThrow() {
        assertThatThrownBy(() -> userValidator.validateUsernameAuthetication(""))
                .isInstanceOf(UsernameValidateException.class);
    }

    @Test
    void validateUsernameAuthetication_WhenTooShort_ShouldThrow() {
        assertThatThrownBy(() -> userValidator.validateUsernameAuthetication("ab"))
                .isInstanceOf(UsernameValidateException.class);
    }

    // --- validateNameClient ---

    @Test
    void validateNameClient_WhenValid_ShouldNotThrow() {
        when(clientService.existsByName("Maria")).thenReturn(false);

        assertThatCode(() -> userValidator.validateNameClient("Maria"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateNameClient_WhenEmpty_ShouldThrow() {
        assertThatThrownBy(() -> userValidator.validateNameClient(""))
                .isInstanceOf(NameValidateException.class)
                .hasMessageContaining("vazio");
    }

    @Test
    void validateNameClient_WhenAlreadyExists_ShouldThrow() {
        when(clientService.existsByName("Maria")).thenReturn(true);

        assertThatThrownBy(() -> userValidator.validateNameClient("Maria"))
                .isInstanceOf(NameValidateException.class)
                .hasMessageContaining("cadastrado");
    }

    @Test
    void validateNameClient_WhenHasNumbers_ShouldThrow() {
        when(clientService.existsByName("Maria123")).thenReturn(false);

        assertThatThrownBy(() -> userValidator.validateNameClient("Maria123"))
                .isInstanceOf(NameValidateException.class)
                .hasMessageContaining("letras");
    }

    @Test
    void validateNameClient_WhenTooShort_ShouldThrow() {
        when(clientService.existsByName("Ab")).thenReturn(false);

        assertThatThrownBy(() -> userValidator.validateNameClient("Ab"))
                .isInstanceOf(NameValidateException.class);
    }
}
