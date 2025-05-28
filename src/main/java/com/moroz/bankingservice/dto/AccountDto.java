package com.moroz.bankingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountDto(
        long id, @JsonProperty("first_name") String firstName, @JsonProperty("first_name") String lastName,
        String email, BigDecimal balance
) {
}
