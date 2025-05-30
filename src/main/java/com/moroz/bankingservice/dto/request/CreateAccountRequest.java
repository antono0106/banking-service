package com.moroz.bankingservice.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moroz.bankingservice.validation.ValidMoneyAmount;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @JsonProperty("first_name")
        @NotBlank(message = "First name must be not null or blank")
        String firstName,
        @JsonProperty("last_name")
        @NotBlank(message = "Last name must be not null or blank")
        String lastName,
        @NotBlank(message = "Email must be not null or blank")
        @Email(message = "Invalid email")
        String email,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("init_balance")
        @DecimalMin(value = "0.00", message = "Balance should be greater than 0")
        @ValidMoneyAmount
        BigDecimal initBalance
) {
}
