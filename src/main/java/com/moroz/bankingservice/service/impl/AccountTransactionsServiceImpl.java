package com.moroz.bankingservice.service.impl;

import com.moroz.bankingservice.dto.request.TransactionRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.entity.Account;
import com.moroz.bankingservice.exception.AccountNotFoundException;
import com.moroz.bankingservice.exception.BadRequestException;
import com.moroz.bankingservice.mapper.AccountMapper;
import com.moroz.bankingservice.repository.AccountRepository;
import com.moroz.bankingservice.service.AbstractAccountService;
import com.moroz.bankingservice.service.AccountTransactionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class AccountTransactionsServiceImpl extends AbstractAccountService implements AccountTransactionsService {

    public AccountTransactionsServiceImpl(
            final AccountRepository accountRepository, final AccountMapper accountMapper
    ) {
        super(accountRepository, accountMapper);
    }

    @Override
    public TransactionResponse deposit(final long id, final TransactionRequest request) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        final BigDecimal amountToDeposit = request.amount();
        log.info("Depositing {} to account with id {}", amountToDeposit, id);
        final BigDecimal depositResult = account.getBalance().add(request.amount());
        accountMapper.updateBalance(depositResult, account);
        final Account savedAccount = accountRepository.save(account);
        log.info("Deposited {} to account with id {}", amountToDeposit, id);
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    public TransactionResponse withdraw(final long id, final TransactionRequest request) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        final BigDecimal amountToWithdraw = request.amount();
        if (amountToWithdraw.compareTo(account.getBalance()) > 0) {
            throw new BadRequestException("Insufficient balance for account with id %d".formatted(id));
        }

        log.info("Withdrawing {} from account with id {}...", amountToWithdraw, id);
        final BigDecimal withdrawResult = account.getBalance().subtract(request.amount());
        accountMapper.updateBalance(withdrawResult, account);
        final Account savedAccount = accountRepository.save(account);
        log.info("Withdraw {} from account with id {} completed", amountToWithdraw, id);
        return accountMapper.toResponse(savedAccount);
    }
}
