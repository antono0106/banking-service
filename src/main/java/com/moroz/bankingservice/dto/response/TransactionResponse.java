package com.moroz.bankingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record TransactionResponse(Long id, @JsonProperty("current_balance") BigDecimal currentBalance) {
}
