package com.moroz.bankingservice.service.impl;

import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.AccountPageImpl;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.entity.Account;
import com.moroz.bankingservice.mapper.AccountMapper;
import com.moroz.bankingservice.repository.AccountRepository;
import com.moroz.bankingservice.service.AbstractAccountService;
import com.moroz.bankingservice.service.AccountManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AccountManagementServiceImpl extends AbstractAccountService implements AccountManagementService {

    public AccountManagementServiceImpl(final AccountRepository accountRepository, final AccountMapper accountMapper) {
        super(accountRepository, accountMapper);
    }

    @Override
    public Page<AccountDto> getAllAccounts(final int page, final int size) {
        log.info("Retrieving all accounts, page {}, size {}", page, size);
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        final List<AccountDto> accounts = accountRepository.findAll(pageRequest)
                .stream()
                .map(accountMapper::toDto)
                .toList();
        log.info("Retrieved {} accounts", accounts.size());
        return new AccountPageImpl<>(accounts, pageRequest, accountRepository.count());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AccountDto createAccount(final CreateAccountRequest request) {
        if (!accountRepository.existsByEmail(request.email())) {
            final Account account = accountMapper.fromRequest(request);
            final Account savedAccount = accountRepository.save(account);
            return accountMapper.toDto(savedAccount);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Account with email %s already exists".formatted(request.email())
            );
        }
    }

    @Override
    public AccountDto getAccountById(final long id) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account with id %s not found".formatted(id))
                );
        return accountMapper.toDto(account);
    }
}
