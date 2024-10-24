package com.api.rest.conveniencestore.validations;

import com.api.rest.conveniencestore.exceptions.NameValidateException;
import com.api.rest.conveniencestore.exceptions.UsernameValidateException;
import com.api.rest.conveniencestore.service.ClientService;
import com.api.rest.conveniencestore.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    private static final String REGEX_NAME_USER = "^[a-zA-Z]+[0-9]{0,3}$";
    private static final String ERROR_LENGTH_USERNAME = "Username must be between 3 and 20 characters:";
    private static final String ERROR_PATTERN_USERNAME = "O username deve começar com uma letra, pode conter até 3 números e não deve incluir caracteres especiais ou espaços.";
    private static final String USERNAME_ALREADY_REGISTERED = "Usuário já cadastrado com o nome: ";
    private static final String REGEX_NAME_CUSTOMER = "^[a-zA-Z]{3,}$";
    public static final String CUSTOMER_ALREADY_REGISTERED = "Cliente com o nome já está cadastrado: ";
    public static final String USERNAME_CANNOT_BE_EMPTY = "O username não pode ser vazio.";
    public static final String CUSTOMER_NAME_CANNOT_BE_EMPTY = "O nome do cliente não pode ser vazio.";
    private static final String ERROR_PATTERN_NAME = "O nome deve conter apenas letras, com minimo três caracteres.";


    private final UserService userService;

    private final ClientService clientService;

    public UserValidator(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    public void validateUsername(String username) throws UsernameValidateException {

        if (username.isEmpty()) {
            throw new UsernameValidateException(USERNAME_CANNOT_BE_EMPTY);
        }

        if (username.length() < 3) {
            throw new UsernameValidateException(ERROR_LENGTH_USERNAME);
        }

        if (!username.matches(REGEX_NAME_USER)) {
            throw new UsernameValidateException(ERROR_PATTERN_USERNAME);
        }

        if (userService.existsByUsername(username)) {
            throw new UsernameValidateException(USERNAME_ALREADY_REGISTERED + username);
        }
    }

    public void validateUsernameAuthetication(String username) throws UsernameValidateException {

        if (username.isEmpty()) {
            throw new UsernameValidateException(USERNAME_CANNOT_BE_EMPTY);
        }

        if (username.length() < 3) {
            throw new UsernameValidateException(ERROR_LENGTH_USERNAME);
        }

        if (!username.matches(REGEX_NAME_USER)) {
            throw new UsernameValidateException(ERROR_PATTERN_USERNAME);
        }
    }

    public void validateNameClient(String name) throws NameValidateException {
        if (name.isEmpty()) {
            throw new NameValidateException(CUSTOMER_NAME_CANNOT_BE_EMPTY);
        }

        if (clientService.existsByName(name)) {
            throw new NameValidateException(CUSTOMER_ALREADY_REGISTERED + name);
        }

        if (!name.matches(REGEX_NAME_CUSTOMER)) {
            throw new NameValidateException(ERROR_PATTERN_NAME);
        }
    }
}