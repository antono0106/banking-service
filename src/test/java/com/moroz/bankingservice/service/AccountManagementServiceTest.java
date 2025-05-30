package com.moroz.bankingservice.service;

import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.entity.Account;
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
import org.springframework.web.server.ResponseStatusException;

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
        account = new Account();
        account.setId(1L);
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setEmail("john@doe.com");
        account.setBalance(123);
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
        assertThrows(ResponseStatusException.class, () -> accountManagementService.createAccount(createAccountRequest));

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

        assertThrows(ResponseStatusException.class, () -> accountManagementService.getAccountById(2L));

        verify(accountRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(accountMapper);
    }

    @Test
    void shouldReturnListOfAccounts() {
        final Account additionalAccount = new Account();
        additionalAccount.setId(2L);
        additionalAccount.setFirstName("John");
        additionalAccount.setLastName("Smith");
        additionalAccount.setEmail("john@smith.com");
        additionalAccount.setBalance(BigDecimal.valueOf(567.89));

        given(accountRepository.findAll(PageRequest.of(0, 10)))
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

        verify(accountRepository, times(1)).findAll(PageRequest.of(0, 10));
        verify(accountRepository, times(1)).count();
        verify(accountMapper, times(2)).toDto(any(Account.class));
    }
}
