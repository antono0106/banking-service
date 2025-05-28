package com.moroz.bankingservice.util;

import com.moroz.bankingservice.entity.Account;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BalanceUtilTest {

    @Test
    void shouldCorrectlyParseBalance() {
        assertEquals(new BigDecimal("123.56"), BalanceUtils.parseFromBalanceAndCents(123, 56));
    }

    @Test
    void shouldCorrectlyParseFromAccount() {
        final Account account = new Account();
        account.setBalance(123);
        account.setCents(56);
        assertEquals(new BigDecimal("123.56"), BalanceUtils.parseFromAccount(account));
    }

    @Test
    void shouldCorrectlyGetBalanceFromAmount() {
        final BigDecimal expectedBalance = new BigDecimal("123.56");
        assertEquals(123, BalanceUtils.getBalanceFromAmount(expectedBalance));
    }

    @Test
    void shouldCorrectlyGetCentsFromAmount() {
        final BigDecimal expectedBalance = new BigDecimal("123.56");
        assertEquals(56, BalanceUtils.getCentsFromAmount(expectedBalance));
    }

    @Test
    void shouldCorrectlyGetZeroCentsFromAmount() {
        final BigDecimal expectedBalance = new BigDecimal("123.00");
        assertEquals(0, BalanceUtils.getCentsFromAmount(expectedBalance));
    }

    @Test
    void shouldThrowExceptionWhenGettingBalanceFromAmount() {
        final BigDecimal expectedBalance = new BigDecimal("-123.56");
        assertThrows(IllegalArgumentException.class, () -> BalanceUtils.getBalanceFromAmount(expectedBalance));
        assertThrows(IllegalArgumentException.class, () -> BalanceUtils.getCentsFromAmount(expectedBalance));
    }
}
