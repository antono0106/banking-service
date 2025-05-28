package com.moroz.bankingservice.dto.request;

import com.moroz.bankingservice.validation.ValidMoneyAmount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        Long sourceAccountId, Long targetAccountId,
        @DecimalMin(value = "0.01") @ValidMoneyAmount @NotNull BigDecimal amount
) {
}
