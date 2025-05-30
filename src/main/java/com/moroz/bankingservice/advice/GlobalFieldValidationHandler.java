package com.moroz.bankingservice.advice;

import com.moroz.bankingservice.dto.response.InvalidFieldsResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalFieldValidationHandler {

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
}
