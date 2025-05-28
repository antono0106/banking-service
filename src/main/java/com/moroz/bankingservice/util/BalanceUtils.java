package com.moroz.bankingservice.util;

import com.moroz.bankingservice.entity.Account;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class BalanceUtils {

    public BigDecimal parseFromBalanceAndCents(final long balance, final long cents) {
        if (balance < 0 || cents < 0 || cents > 99) {
            throw new IllegalArgumentException("Invalid balance %d and cents %d".formatted(balance, cents));
        }
        return new BigDecimal("%d.%d".formatted(balance, cents)).setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal parseFromAccount(final Account account) {
        return parseFromBalanceAndCents(account.getBalance(), account.getCents());
    }

    public long getBalanceFromAmount(final BigDecimal amount) {
        if (amount.toBigInteger().longValue() < 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return amount.toBigInteger().longValue();
    }

    public int getCentsFromAmount(final BigDecimal amount) {
        if (amount.doubleValue() < 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        final int cents = amount
                .remainder(BigDecimal.ONE)
                .movePointRight(amount.scale())
                .abs().intValue();
        if (cents < 0 || cents > 99) {
            throw new IllegalArgumentException("Cents must be greater than 0 and less than 99");
        }
        return cents;
    }
}
