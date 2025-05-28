package com.moroz.bankingservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidMoneyAmountValidator implements ConstraintValidator<ValidMoneyAmount, BigDecimal> {
    private static final Pattern PATTERN = Pattern.compile("\\d+(\\.\\d{1,2})?");

    @Override
    public boolean isValid(BigDecimal bigDecimal, ConstraintValidatorContext constraintValidatorContext) {
        if (bigDecimal == null) return true;

        return PATTERN.matcher(bigDecimal.toString()).matches();
    }
}
