package com.moroz.bankingservice.dto.request;

import com.moroz.bankingservice.validation.ValidMoneyAmount;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record TransactionRequest(@DecimalMin(value = "0.01") @ValidMoneyAmount BigDecimal amount) {
}
