package com.moroz.bankingservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ValidMoneyAmountValidator implements ConstraintValidator<ValidMoneyAmount, BigDecimal> {

    @Override
    public boolean isValid(BigDecimal bigDecimal, ConstraintValidatorContext constraintValidatorContext) {
        if (bigDecimal == null) return true;

        return bigDecimal.scale() >= 0 && bigDecimal.scale() <= 2;
    }
}
