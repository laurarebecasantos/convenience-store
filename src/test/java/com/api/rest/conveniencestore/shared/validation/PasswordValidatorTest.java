package com.api.rest.conveniencestore.shared.validation;

import com.api.rest.conveniencestore.shared.exception.PasswordValidateException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void validatePassword_WhenValid_ShouldNotThrow() {
        assertThatCode(() -> PasswordValidator.validatePassword("Password1"))
                .doesNotThrowAnyException();
    }

    @Test
    void validatePassword_WhenTooShort_ShouldThrow() {
        assertThatThrownBy(() -> PasswordValidator.validatePassword("Pass1"))
                .isInstanceOf(PasswordValidateException.class)
                .hasMessageContaining("8 caracteres");
    }

    @Test
    void validatePassword_WhenNoUppercase_ShouldThrow() {
        assertThatThrownBy(() -> PasswordValidator.validatePassword("password1"))
                .isInstanceOf(PasswordValidateException.class)
                .hasMessageContaining("maiúscula");
    }

    @Test
    void validatePassword_WhenNoLowercase_ShouldThrow() {
        assertThatThrownBy(() -> PasswordValidator.validatePassword("PASSWORD1"))
                .isInstanceOf(PasswordValidateException.class)
                .hasMessageContaining("minúscula");
    }

    @Test
    void validatePassword_WhenNoDigit_ShouldThrow() {
        assertThatThrownBy(() -> PasswordValidator.validatePassword("PasswordABC"))
                .isInstanceOf(PasswordValidateException.class)
                .hasMessageContaining("número");
    }

    @Test
    void validatePassword_WhenExactly8CharsValid_ShouldNotThrow() {
        assertThatCode(() -> PasswordValidator.validatePassword("Pass1234"))
                .doesNotThrowAnyException();
    }
}
