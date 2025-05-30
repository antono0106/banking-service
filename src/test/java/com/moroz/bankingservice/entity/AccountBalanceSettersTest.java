package com.moroz.bankingservice.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountBalanceSettersTest {

    private final Account account = new Account();

    @Test
    void shouldConvertLongToBigDecimal() {
        account.setBalance(1000);
        assertEquals(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP), account.getBalance());
    }
}
