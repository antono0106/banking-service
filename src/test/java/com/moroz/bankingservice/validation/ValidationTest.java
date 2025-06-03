package com.moroz.bankingservice.validation;

import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.dto.request.TransactionRequest;
import com.moroz.bankingservice.dto.request.TransferRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class ValidationTest {

    private Validator validator;

    @BeforeEach
    void initValidator() {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        validatorFactory.close();
    }


    @Test
    void shouldFailWhenValueValueIsNegative() {
        final CreateAccountRequest request = new CreateAccountRequest(
                "John", "Doe", "john@doe.com", BigDecimal.valueOf(-1).setScale(2, RoundingMode.HALF_UP)
        );

        final Set<ConstraintViolation<CreateAccountRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertEquals("Balance should be greater than or equal to 0.00", constraintViolations.stream().findFirst().get().getMessage());
    }

    @Test
    void shouldFailWhenValueValueIsZeroWithoutScale() {
        final CreateAccountRequest request = new CreateAccountRequest(
                "John", "Doe", "john@doe.com", BigDecimal.ONE.setScale(3, RoundingMode.HALF_UP)
        );

        final Set<ConstraintViolation<CreateAccountRequest>> constraintViolations = validator.validate(request);
        assertEquals(1, constraintViolations.size());
        assertTrue(constraintViolations.stream()
                .anyMatch(
                        violation ->
                                violation.getMessage()
                                        .equals("Value must be integer or float within 0 or 2 decimal places")));
    }

    @Test
    void shouldFailWhenNonNullValueAreNull() {
        final CreateAccountRequest request = new CreateAccountRequest("", " ", null, null);
        final Set<ConstraintViolation<CreateAccountRequest>> constraintViolations = validator.validate(request);
        assertEquals(3, constraintViolations.size());
        assertTrue(constraintViolations.stream().allMatch(v -> v.getMessage().contains("must be not null or blank")));
    }

    @Test
    void shouldFailWhenInvalidEmail() {
        final CreateAccountRequest request = new CreateAccountRequest(
                "John", "Doe", "john.doe", null
        );
        final Set<ConstraintViolation<CreateAccountRequest>> constraintViolations = validator.validate(request);
        assertEquals(1, constraintViolations.size());
        assertEquals("Invalid email", constraintViolations.stream().findFirst().get().getMessage());
    }

    @Test
    void shouldBeValidForTransferAndTransactionRequest() {
        final TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.valueOf(123));
        final Set<ConstraintViolation<TransactionRequest>> transactionViolation = validator.validate(transactionRequest);
        assertTrue(transactionViolation.isEmpty());

        final TransferRequest transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(123));
        final Set<ConstraintViolation<TransferRequest>> transferViolation = validator.validate(transferRequest);
        assertTrue(transferViolation.isEmpty());
    }

    @Test
    void shouldFailForZeroAmount() {
        final TransactionRequest transactionRequest = new TransactionRequest(BigDecimal.ZERO);
        final Set<ConstraintViolation<TransactionRequest>> transactionViolation = validator.validate(transactionRequest);
        assertEquals(1, transactionViolation.size());

        final TransferRequest transferRequest = new TransferRequest(1L, 2L, BigDecimal.ZERO);
        final Set<ConstraintViolation<TransferRequest>> transferViolation = validator.validate(transferRequest);
        assertEquals(1, transferViolation.size());
    }

    @Test
    void shouldFailForNulls() {
        final TransactionRequest transactionRequest = new TransactionRequest(null);
        final Set<ConstraintViolation<TransactionRequest>> transactionViolation = validator.validate(transactionRequest);
        assertEquals(1, transactionViolation.size());

        final TransferRequest transferRequest = new TransferRequest(null, null, null);
        final Set<ConstraintViolation<TransferRequest>> transferViolation = validator.validate(transferRequest);
        assertEquals(3, transferViolation.size());
    }
}
