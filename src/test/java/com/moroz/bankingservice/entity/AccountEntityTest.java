package com.moroz.bankingservice.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountEntityTest {

    @Test
    void shouldConvertLongToBigDecimal() {
        final Account account = new Account();
        account.setBalance(1000);
        assertEquals(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP), account.getBalance());
    }

    @Test
    void shouldConvertLongToBigDecimalFromConstructor() {
        final Account account = new Account(1L, "John", "Doe", "john.doe@gmail.com", 1000);
        assertEquals(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP), account.getBalance());
    }
}
