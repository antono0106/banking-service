package com.moroz.bankingservice.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(final String existingEmail) {
        super("Account with email %s already exists".formatted(existingEmail));
    }
}
