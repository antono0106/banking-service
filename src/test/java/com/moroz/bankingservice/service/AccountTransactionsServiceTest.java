package com.moroz.bankingservice.service;

import com.moroz.bankingservice.dto.request.TransactionRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.entity.Account;
import com.moroz.bankingservice.mapper.AccountMapper;
import com.moroz.bankingservice.repository.AccountRepository;
import com.moroz.bankingservice.service.impl.AccountTransactionsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountTransactionsServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountTransactionsServiceImpl accountManagementService;

    private Account account;

    @BeforeEach
    void initAccount() {
        account = new Account(1L, "John", "Doe", "john@doe.com", 123);
    }

    @Test
    void shouldDeposit() {
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        final TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(567));
        final BigDecimal resultBalance = account.getBalance().add(request.amount());

        doAnswer(inv -> {
            final BigDecimal amount = inv.getArgument(0);
            final Account accountToUpdate = inv.getArgument(1);

            accountToUpdate.setBalance(amount);
            return null;
        }).when(accountMapper).updateBalance(resultBalance, account);

        final Account saved = new Account(
                1L, account.getFirstName(), account.getLastName(), account.getEmail(), resultBalance
        );
        given(accountRepository.save(any())).willReturn(saved);

        final TransactionResponse mappedResponse = new TransactionResponse(saved.getId(), saved.getBalance());
        given(accountMapper.toResponse(saved)).willReturn(mappedResponse);

        final TransactionResponse response = accountManagementService.deposit(account.getId(), request);
        assertNotNull(response);
        assertEquals(resultBalance, response.currentBalance());

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).updateBalance(resultBalance, account);
        verify(accountMapper, times(1)).toResponse(account);
    }

    @Test
    void shouldWithdraw() {
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        final TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(23));
        final BigDecimal resultBalance = account.getBalance().subtract(request.amount());

        doAnswer(inv -> {
            final BigDecimal amount = inv.getArgument(0);
            final Account accountToUpdate = inv.getArgument(1);

            accountToUpdate.setBalance(amount);
            return null;
        }).when(accountMapper).updateBalance(resultBalance, account);

        final Account saved = new Account(
                1L, account.getFirstName(), account.getLastName(), account.getEmail(), resultBalance
        );

        given(accountRepository.save(any())).willReturn(saved);

        final TransactionResponse mappedResponse = new TransactionResponse(saved.getId(), saved.getBalance());
        given(accountMapper.toResponse(saved)).willReturn(mappedResponse);

        final TransactionResponse response = accountManagementService.withdraw(account.getId(), request);
        assertNotNull(response);
        assertEquals(resultBalance, response.currentBalance());

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).updateBalance(resultBalance, account);
        verify(accountMapper, times(1)).toResponse(account);
    }

    @Test
    void shouldFailWhenAmountIsGreaterThanBalance() {
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        final TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(567));

        assertThrows(ResponseStatusException.class, () -> accountManagementService.withdraw(account.getId(), request));

        verify(accountRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(accountMapper);
    }

}
