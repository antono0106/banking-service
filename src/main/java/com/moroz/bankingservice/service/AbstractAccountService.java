package com.moroz.bankingservice.service;

import com.moroz.bankingservice.mapper.AccountMapper;
import com.moroz.bankingservice.repository.AccountRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractAccountService {
    protected final AccountRepository accountRepository;
    protected final AccountMapper accountMapper;
}
