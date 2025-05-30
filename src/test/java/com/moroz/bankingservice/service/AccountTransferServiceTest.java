package com.moroz.bankingservice.service;

import com.moroz.bankingservice.dto.request.TransferRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.dto.response.TransferResponse;
import com.moroz.bankingservice.entity.Account;
import com.moroz.bankingservice.mapper.AccountMapper;
import com.moroz.bankingservice.repository.AccountRepository;
import com.moroz.bankingservice.service.impl.AccountTransactionsServiceImpl;
import com.moroz.bankingservice.service.impl.AccountTransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountTransferServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @InjectMocks
    private AccountTransactionsServiceImpl accountTransactionsService;

    private AccountTransferService accountTransferService;

    private Account accountA;
    private Account accountB;

    @BeforeEach
    void initTransferService() {
        accountTransferService = new AccountTransferServiceImpl(accountTransactionsService);
        accountA = new Account(
                1L, "John", "Doe", "john@doe.com", 123
        );
        accountB = new Account(
                2L, "Jane", "Doe", "jane@doe.com", BigDecimal.valueOf(567.89)
        );
    }

    @Test
    void shouldSuccessfullyTransferBetweenAccounts() {
        final TransferRequest request = new TransferRequest(accountA.getId(), accountB.getId(), BigDecimal.valueOf(23));

        given(accountRepository.findById(1L)).willReturn(Optional.of(accountA));
        final BigDecimal accountANewBalance = accountA.getBalance().subtract(request.amount());

        final Answer<?> updateBalanceAnswer = inv -> {
            final BigDecimal amount = inv.getArgument(0);
            final Account accountToUpdate = inv.getArgument(1);

            accountToUpdate.setBalance(amount);
            return null;
        };

        doAnswer(updateBalanceAnswer).when(accountMapper).updateBalance(accountANewBalance, accountA);
        final Account savedAccountA = new Account(
                accountA.getId(), accountA.getFirstName(), accountA.getLastName(), accountA.getEmail(), accountANewBalance
        );
        given(accountRepository.save(accountA)).willReturn(savedAccountA);
        final TransactionResponse transactionResponseA = new TransactionResponse(savedAccountA.getId(), savedAccountA.getBalance());
        given(accountMapper.toResponse(savedAccountA)).willReturn(transactionResponseA);

        given(accountRepository.findById(2L)).willReturn(Optional.of(accountB));
        final BigDecimal accountBNewBalance = accountB.getBalance().add(request.amount());
        doAnswer(updateBalanceAnswer).when(accountMapper).updateBalance(accountBNewBalance, accountB);
        final Account savedAccountB = new Account(
                accountB.getId(), accountB.getFirstName(), accountB.getLastName(), accountB.getEmail(), accountBNewBalance
        );
        given(accountRepository.save(accountB)).willReturn(savedAccountB);
        final TransactionResponse transactionResponseB = new TransactionResponse(savedAccountB.getId(), savedAccountB.getBalance());
        given(accountMapper.toResponse(savedAccountB)).willReturn(transactionResponseB);

        final TransferResponse transferResponse = accountTransferService.transfer(request);

        assertNotNull(transferResponse);
        assertEquals(accountANewBalance, transferResponse.sourceAccount().currentBalance());
        assertEquals(accountBNewBalance, transferResponse.targetAccount().currentBalance());

        verify(accountRepository, times(2)).findById(anyLong());
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(accountMapper, times(2)).updateBalance(any(BigDecimal.class), any(Account.class));
        verify(accountMapper, times(2)).toResponse(any(Account.class));
    }

    @Test
    void shouldFailOnWithdraw() {
        final TransferRequest request = new TransferRequest(accountA.getId(), accountB.getId(), BigDecimal.valueOf(124));

        given(accountRepository.findById(1L)).willReturn(Optional.of(accountA));

        assertThrows(ResponseStatusException.class, () -> accountTransferService.transfer(request));

        verify(accountRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(accountMapper);
    }
}
