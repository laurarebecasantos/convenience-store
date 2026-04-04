package com.api.rest.conveniencestore.shared.validation;

import com.api.rest.conveniencestore.shared.exception.CpfValidateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class CpfValidatorTest {

    @Test
    void validateCpf_WhenValidFormat_ShouldNotThrow() {
        assertThatCode(() -> CpfValidator.validateCpf("123.456.789-09"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678909",      // sem formatação
            "123.456.789-0",    // dígito faltando
            "123456789-09",     // ponto faltando
            "abc.def.ghi-jk",   // letras
            ""                  // vazio
    })
    void validateCpf_WhenInvalidFormat_ShouldThrow(String cpf) {
        assertThatThrownBy(() -> CpfValidator.validateCpf(cpf))
                .isInstanceOf(CpfValidateException.class)
                .hasMessageContaining("formato");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "111.111.111-11",
            "222.222.222-22",
            "333.333.333-33",
            "999.999.999-99"
    })
    void validateCpf_WhenSequentialDigits_ShouldThrow(String cpf) {
        assertThatThrownBy(() -> CpfValidator.validateCpf(cpf))
                .isInstanceOf(CpfValidateException.class)
                .hasMessageContaining("sequencial");
    }
}
