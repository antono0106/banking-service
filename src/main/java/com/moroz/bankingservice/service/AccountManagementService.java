package com.moroz.bankingservice.service;

import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import org.springframework.data.domain.Page;

public interface AccountManagementService {
    Page<AccountDto> getAllAccounts(int page, int size);

    AccountDto createAccount(CreateAccountRequest request);

    AccountDto getAccountById(long id);
}
