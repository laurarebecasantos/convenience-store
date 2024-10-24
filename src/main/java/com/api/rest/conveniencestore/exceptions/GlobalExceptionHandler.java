package com.api.rest.conveniencestore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity userRegistrationException(UserRegistrationException except) { //POST USER
        ErrorResponse errorResponse = new ErrorResponse("409", except.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(UserListingNullException.class)
    public ResponseEntity userListingNullException(UserListingNullException except) {
        ErrorResponse errorResponse = new ErrorResponse("404", except.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException except) {
        ErrorResponse errorResponse = new ErrorResponse("404", except.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserEmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> userEmailNotFoundException(UserEmailNotFoundException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserInvalidStatusException.class)
    public ResponseEntity<ErrorResponse> userInvalidRolesException(UserInvalidStatusException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserInvalidRolesException.class)
    public ResponseEntity<ErrorResponse> userInvalidRolesException(UserInvalidRolesException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PasswordValidateException.class)
    public ResponseEntity<ErrorResponse> passwordNotValidException(PasswordValidateException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UsernameValidateException.class)
    public ResponseEntity<ErrorResponse> usernameNotValidException(UsernameValidateException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NameValidateException.class)
    public ResponseEntity<ErrorResponse> nameValidateException(NameValidateException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ProductRegistrationException.class)
    public ResponseEntity productRegistrationException(ProductRegistrationException except) {
        ErrorResponse errorResponse = new ErrorResponse("409", except.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ProductListingNullException.class)
    public ResponseEntity productListingNullException(ProductListingNullException except) {
        ErrorResponse errorResponse = new ErrorResponse("404", except.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> productNotFoundException(ProductNotFoundException except) {
        ErrorResponse errorResponse = new ErrorResponse("404", except.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ProductInvalidStatusException.class)
    public ResponseEntity<ErrorResponse> productInvalidStatusException(ProductInvalidStatusException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ProductInactiveException.class)
    public ResponseEntity<ErrorResponse> productInactiveException(ProductInactiveException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ProductInsufficientStockException.class)
    public ResponseEntity<ErrorResponse> productInsufficientStockException(ProductInsufficientStockException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SaleNotValidPaymentMethodException.class)
    public ResponseEntity<ErrorResponse> saleNotValidPaymentMethodException(SaleNotValidPaymentMethodException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SaleListingNullException.class)
    public ResponseEntity<ErrorResponse> saleListingNullException(SaleListingNullException except) {
        ErrorResponse errorResponse = new ErrorResponse("404", except.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SaleInvalidStatusException.class)
    public ResponseEntity<ErrorResponse> saleInvalidStatusException(SaleInvalidStatusException except) {
        ErrorResponse errorResponse = new ErrorResponse("400", except.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CpfValidateException.class)
    public ResponseEntity<ErrorResponse> clientCpfAlreadyExistsException(CpfValidateException except) {
        ErrorResponse errorResponse = new ErrorResponse("409", except.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(AutheticationException.class)
    public ResponseEntity handleError401(AutheticationException except) {
        ErrorResponse errorResponse = new ErrorResponse("Unauthorized", except.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException500() {
        ErrorResponse errorResponse = new ErrorResponse("500", "An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}