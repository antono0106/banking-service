package com.moroz.bankingservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moroz.bankingservice.validation.ValidMoneyAmount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "Source account id must be not null")
        @JsonProperty("source_account_id")
        Long sourceAccountId,
        @NotNull(message = "Target account id must be not null")
        @JsonProperty("target_account_id")
        Long targetAccountId,
        @NotNull(message = "Amount must be not null")
        @DecimalMin(value = "0.01", message = "Amount should be greater than 0.00")
        @ValidMoneyAmount
        BigDecimal amount
) {
}
