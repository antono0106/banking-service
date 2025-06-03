package com.moroz.bankingservice.advice;

import com.moroz.bankingservice.dto.response.ApiErrorResponse;
import com.moroz.bankingservice.dto.response.InvalidFieldsResponse;
import com.moroz.bankingservice.exception.AccountAlreadyExistsException;
import com.moroz.bankingservice.exception.AccountNotFoundException;
import com.moroz.bankingservice.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Hidden
@RestControllerAdvice
public class AccountsExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public InvalidFieldsResponse handleInvalidFields(final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = ex.getFieldErrors()
                .stream()
                .collect(
                        Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, "%s, %s"::formatted)
                );

        return new InvalidFieldsResponse(Instant.now().toEpochMilli(), BAD_REQUEST.value(), "Invalid input fields", errors);
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ApiErrorResponse handleAccountAlreadyExistsException(final AccountAlreadyExistsException ex) {
        return new ApiErrorResponse(Instant.now().toEpochMilli(), 409, "Conflict", ex.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public ApiErrorResponse handleAccountNotFoundException(final AccountNotFoundException ex) {
        return new ApiErrorResponse(Instant.now().toEpochMilli(), 404, "Not Found", ex.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ApiErrorResponse handleBadRequestException(final BadRequestException ex) {
        return new ApiErrorResponse(Instant.now().toEpochMilli(), 400, "Bad request", ex.getMessage());
    }
}
