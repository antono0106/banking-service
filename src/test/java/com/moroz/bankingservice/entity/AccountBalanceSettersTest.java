package com.moroz.bankingservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountBalanceSettersTest {

    private final Account account = new Account();

    @Test
    void shouldThrowExceptionWhenBalanceIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> account.setBalance(-1));
    }

    @Test
    void shouldThrowExceptionWhenCentsAreNegative() {
        assertThrows(IllegalArgumentException.class, () -> account.setCents(-1));
    }

    @Test
    void shouldThrowWhenCentsAreGreaterThanNinetyNine() {
        assertThrows(IllegalArgumentException.class, () -> account.setCents(100));
    }
}
