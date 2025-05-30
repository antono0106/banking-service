package com.moroz.bankingservice.dto.request;

import com.moroz.bankingservice.validation.ValidMoneyAmount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull(message = "Amount must be not null")
        @DecimalMin(value = "0.01", message = "Amount should be greater than 0.00")
        @ValidMoneyAmount
        BigDecimal amount
) {
}
