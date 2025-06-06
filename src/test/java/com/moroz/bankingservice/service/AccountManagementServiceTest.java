package com.moroz.bankingservice.service;

import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.entity.Account;
import com.moroz.bankingservice.exception.AccountAlreadyExistsException;
import com.moroz.bankingservice.exception.AccountNotFoundException;
import com.moroz.bankingservice.mapper.AccountMapper;
import com.moroz.bankingservice.repository.AccountRepository;
import com.moroz.bankingservice.service.impl.AccountManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountManagementServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountManagementServiceImpl accountManagementService;

    private Account account;

    @BeforeEach
    void initAccount() {
        account = new Account(1L, "John", "Doe", "john@doe.com", 123);
    }

    @Test
    void shouldCreateNewAccount() {
        final CreateAccountRequest createAccountRequest = new CreateAccountRequest(
                account.getFirstName(), account.getLastName(), account.getEmail(), account.getBalance()
        );

        given(accountRepository.existsByEmail(account.getEmail())).willReturn(false);

        given(accountMapper.fromRequest(createAccountRequest)).willReturn(account);

        given(accountRepository.save(account)).willReturn(account);

        final AccountDto mappedDto = new AccountDto(
                account.getId(), account.getFirstName(), account.getLastName(), account.getEmail(), account.getBalance()
        );
        given(accountMapper.toDto(account))
                .willReturn(mappedDto);

        final AccountDto result = accountManagementService.createAccount(createAccountRequest);

        assertNotNull(result);
        assertEquals(mappedDto, result);
    }

    @Test
    void shouldThrowWhenAccountExists() {
        final CreateAccountRequest createAccountRequest = new CreateAccountRequest(
                account.getFirstName(), account.getLastName(), account.getEmail(), account.getBalance()
        );

        given(accountRepository.existsByEmail(account.getEmail())).willReturn(true);
        assertThrows(AccountAlreadyExistsException.class, () -> accountManagementService.createAccount(createAccountRequest));

        verify(accountRepository, times(1)).existsByEmail(account.getEmail());
        verify(accountRepository, never()).save(account);
        verifyNoMoreInteractions(accountMapper);
    }

    @Test
    void shouldReturnAccountById() {
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        final AccountDto mappedDto = new AccountDto(
                account.getId(), account.getFirstName(), account.getLastName(), account.getEmail(), account.getBalance()
        );

        given(accountMapper.toDto(account))
                .willReturn(mappedDto);

        final AccountDto result = accountManagementService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(mappedDto, result);

        verify(accountRepository, times(1)).findById(1L);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void shouldNotReturnAccountById() {
        given(accountRepository.findById(2L)).willReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountManagementService.getAccountById(2L));

        verify(accountRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(accountMapper);
    }

    @Test
    void shouldReturnListOfAccounts() {
        final Account additionalAccount = new Account(
                2L, "John", "Smith", "john@smith.com", BigDecimal.valueOf(567.89)
        );

        given(accountRepository.findAll(PageRequest.of(0, 10, Sort.Direction.ASC, "id")))
                .willReturn(new PageImpl<>(List.of(account, additionalAccount)));

        final AccountDto firstDto = new AccountDto(
                account.getId(), account.getFirstName(), account.getLastName(), account.getEmail(), account.getBalance()
        );
        final AccountDto secondDto = new AccountDto(
                additionalAccount.getId(), additionalAccount.getFirstName(), additionalAccount.getLastName(),
                additionalAccount.getEmail(), additionalAccount.getBalance()
        );
        given(accountMapper.toDto(account)).willReturn(firstDto);
        given(accountMapper.toDto(additionalAccount)).willReturn(secondDto);

        given(accountRepository.count()).willReturn(2L);
        final Page<AccountDto> response = accountManagementService.getAllAccounts(0, 10);
        assertNotNull(response);
        assertEquals(2, response.getNumberOfElements());

        verify(accountRepository, times(1)).findAll(any(PageRequest.class));
        verify(accountRepository, times(1)).count();
        verify(accountMapper, times(2)).toDto(any(Account.class));
    }
}
