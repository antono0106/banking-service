package com.moroz.bankingservice.service.impl;

import com.moroz.bankingservice.dto.request.TransactionRequest;
import com.moroz.bankingservice.dto.request.TransferRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.dto.response.TransferResponse;
import com.moroz.bankingservice.service.AccountTransactionsService;
import com.moroz.bankingservice.service.AccountTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE)
public class AccountTransferServiceImpl implements AccountTransferService {
    private final AccountTransactionsService accountTransactionsService;

    @Override
    public TransferResponse transfer(final TransferRequest transferRequest) {
        final BigDecimal amount = transferRequest.amount();
        final TransactionRequest transactionRequest = new TransactionRequest(amount);

        log.info("Started transfer between account with id {} and account with id {}, amount to transfer: {}",
                transferRequest.sourceAccountId(), transferRequest.targetAccountId(), amount);
        final TransactionResponse withdrawResponse = accountTransactionsService
                .withdraw(transferRequest.sourceAccountId(), transactionRequest);
        final TransactionResponse depositResponse = accountTransactionsService
                .deposit(transferRequest.targetAccountId(), transactionRequest);
        log.info("Transferring amount {} between account with id {} and account with id {} completed",
                amount, withdrawResponse.id(), depositResponse.id());

        return new TransferResponse(withdrawResponse, depositResponse);
    }
}
