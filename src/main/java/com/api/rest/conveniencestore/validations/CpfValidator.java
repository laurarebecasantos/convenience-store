package com.api.rest.conveniencestore.validations;

import com.api.rest.conveniencestore.exceptions.CpfValidateException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CpfValidator {

    private static final String REGEX_CPF =  "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$";
    private static final String REGEX_SEQUENTIAL_CPF = "^(\\d)\\1{10}$"; // Sequenciais 111.111.111-11 ~ 222.222.222-22
    private static final String ERROR_INVALID_FORMAT = "O CPF deve conter exatamente 11 dígitos e estar no formato 000.000.000-00";
    private static final String ERROR_SEQUENTIAL_CPF = "O CPF não pode ser um número sequencial: ";

    public static void validateCpf(String cpf) throws CpfValidateException {
        if (!Pattern.matches(REGEX_CPF, cpf)) {
            throw new CpfValidateException(ERROR_INVALID_FORMAT);
        }

        String unformattedCpf = cpf.replaceAll("\\D", "");

        if (Pattern.matches(REGEX_SEQUENTIAL_CPF, unformattedCpf)) {
            throw new CpfValidateException(ERROR_SEQUENTIAL_CPF + cpf);
        }
    }
}
