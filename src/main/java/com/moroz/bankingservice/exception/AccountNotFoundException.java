package com.moroz.bankingservice.exception;


public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(final Long nonExistingAccountId) {
        super("Account with id %d not found".formatted(nonExistingAccountId));
    }
}
