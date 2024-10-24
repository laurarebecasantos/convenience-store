package com.api.rest.conveniencestore.validations;

import com.api.rest.conveniencestore.exceptions.PasswordValidateException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    private static final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    private static final String ERROR_LENGTH_PASSWORD = "A senha deve ter pelo menos 8 caracteres.";
    private static final String ERROR_PATTERN_PASSWORD = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula e um número.";

    public static void validatePassword(String password) throws PasswordValidateException {
        if (password.length() < 8) {
            throw new PasswordValidateException(ERROR_LENGTH_PASSWORD);
        }

        if (!password.matches(REGEX_PASSWORD)) {
            throw new PasswordValidateException(ERROR_PATTERN_PASSWORD);
        }
    }
}