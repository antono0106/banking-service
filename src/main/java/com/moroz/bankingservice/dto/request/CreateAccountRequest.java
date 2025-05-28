package com.moroz.bankingservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moroz.bankingservice.validation.ValidMoneyAmount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @JsonProperty("first_name")
        @NotNull(message = "First name must be not null")
        @NotBlank(message = "First name must be not blank")
        String firstName,
        @JsonProperty("first_name")
        @NotNull(message = "Last name must be not null")
        @NotBlank(message = "Last name must be not blank")
        String lastName,
        @NotNull(message = "Email must be not null")
        @NotBlank(message = "Email must be not blank")
        @Email(message = "Invalid email")
        String email,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("init_balance")
        @DecimalMin(value = "0.0", message = "Balance should be greater than 0")
        @ValidMoneyAmount
        BigDecimal initBalance
) {
}
