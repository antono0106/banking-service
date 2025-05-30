package com.moroz.bankingservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountDto(
        long id, @JsonProperty("first_name") String firstName, @JsonProperty("last_name") String lastName,
        String email, BigDecimal balance
) {
}
