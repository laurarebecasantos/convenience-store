package com.api.rest.conveniencestore.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> userRegistrationException(UserRegistrationException except) {
        return build(HttpStatus.CONFLICT, "CONFLICT", except.getMessage());
    }

    @ExceptionHandler(UserListingNullException.class)
    public ResponseEntity<ErrorResponse> userListingNullException(UserListingNullException except) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", except.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException except) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", except.getMessage());
    }

    @ExceptionHandler(UserEmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> userEmailNotFoundException(UserEmailNotFoundException except) {
        return build(HttpStatus.CONFLICT, "CONFLICT", except.getMessage());
    }

    @ExceptionHandler(UserInvalidStatusException.class)
    public ResponseEntity<ErrorResponse> userInvalidStatusException(UserInvalidStatusException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_STATUS", except.getMessage());
    }

    @ExceptionHandler(UserInvalidRolesException.class)
    public ResponseEntity<ErrorResponse> userInvalidRolesException(UserInvalidRolesException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_ROLE", except.getMessage());
    }

    @ExceptionHandler(PasswordValidateException.class)
    public ResponseEntity<ErrorResponse> passwordNotValidException(PasswordValidateException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", except.getMessage());
    }

    @ExceptionHandler(UsernameValidateException.class)
    public ResponseEntity<ErrorResponse> usernameNotValidException(UsernameValidateException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_USERNAME", except.getMessage());
    }

    @ExceptionHandler(NameValidateException.class)
    public ResponseEntity<ErrorResponse> nameValidateException(NameValidateException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_NAME", except.getMessage());
    }

    @ExceptionHandler(ProductRegistrationException.class)
    public ResponseEntity<ErrorResponse> productRegistrationException(ProductRegistrationException except) {
        return build(HttpStatus.CONFLICT, "CONFLICT", except.getMessage());
    }

    @ExceptionHandler(ProductListingNullException.class)
    public ResponseEntity<ErrorResponse> productListingNullException(ProductListingNullException except) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", except.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> productNotFoundException(ProductNotFoundException except) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", except.getMessage());
    }

    @ExceptionHandler(ProductInvalidStatusException.class)
    public ResponseEntity<ErrorResponse> productInvalidStatusException(ProductInvalidStatusException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_STATUS", except.getMessage());
    }

    @ExceptionHandler(ProductInactiveException.class)
    public ResponseEntity<ErrorResponse> productInactiveException(ProductInactiveException except) {
        return build(HttpStatus.BAD_REQUEST, "PRODUCT_INACTIVE", except.getMessage());
    }

    @ExceptionHandler(ProductInsufficientStockException.class)
    public ResponseEntity<ErrorResponse> productInsufficientStockException(ProductInsufficientStockException except) {
        return build(HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK", except.getMessage());
    }

    @ExceptionHandler(ProductDateInvalidException.class)
    public ResponseEntity<ErrorResponse> productDateInvalidException(ProductDateInvalidException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_DATE", except.getMessage());
    }

    @ExceptionHandler(SaleNotValidPaymentMethodException.class)
    public ResponseEntity<ErrorResponse> saleNotValidPaymentMethodException(SaleNotValidPaymentMethodException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_PAYMENT_METHOD", except.getMessage());
    }

    @ExceptionHandler(SaleListingNullException.class)
    public ResponseEntity<ErrorResponse> saleListingNullException(SaleListingNullException except) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", except.getMessage());
    }

    @ExceptionHandler(SaleInvalidStatusException.class)
    public ResponseEntity<ErrorResponse> saleInvalidStatusException(SaleInvalidStatusException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_STATUS", except.getMessage());
    }

    @ExceptionHandler(CpfValidateException.class)
    public ResponseEntity<ErrorResponse> clientCpfAlreadyExistsException(CpfValidateException except) {
        return build(HttpStatus.CONFLICT, "CONFLICT", except.getMessage());
    }

    @ExceptionHandler(ClientCpfNotFoundException.class)
    public ResponseEntity<ErrorResponse> clientCpfNotFoundException(ClientCpfNotFoundException except) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", except.getMessage());
    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> clientAlreadyExistsException(ClientAlreadyExistsException except) {
        return build(HttpStatus.CONFLICT, "CONFLICT", except.getMessage());
    }

    @ExceptionHandler(AutheticationException.class)
    public ResponseEntity<ErrorResponse> autheticationException(AutheticationException except) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", except.getMessage());
    }

    @ExceptionHandler(AutheticationInvalidException.class)
    public ResponseEntity<ErrorResponse> autheticationInvalidException(AutheticationInvalidException except) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", except.getMessage());
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ErrorResponse> tokenGenerationException(TokenGenerationException except) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_ERROR", except.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER",
                "Valor inválido para o parâmetro '" + except.getName() + "': '" + except.getValue() + "'. Era esperado um número inteiro.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException except) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", "Registro com o ID informado não foi encontrado.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST_BODY",
                "Formato do corpo da requisição inválido. Verifique se o JSON está correto e se os tipos dos campos estão corretos.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException except) {
        String fields = except.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Dados inválidos: " + fields);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentException(IllegalArgumentException except) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", except.getMessage());
    }

    @ExceptionHandler(LoyaltyException.class)
    public ResponseEntity<ErrorResponse> loyaltyException(LoyaltyException except) {
        return build(HttpStatus.BAD_REQUEST, "LOYALTY_ERROR", except.getMessage());
    }

    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLock() {
        return build(HttpStatus.CONFLICT, "OPTIMISTIC_LOCK_CONFLICT",
                "O registro foi modificado por outra operação simultaneamente. Tente novamente.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException500() {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Ocorreu um erro inesperado. Tente novamente.");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus httpStatus, String error, String message) {
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(error, message, httpStatus.value()));
    }
}
