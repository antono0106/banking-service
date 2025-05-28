package com.moroz.bankingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferResponse(
        @JsonProperty("source_account") TransactionResponse sourceAccount,
        @JsonProperty("target_account") TransactionResponse targetAccount
) {
}
