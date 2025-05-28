package com.moroz.bankingservice.service.impl;

import com.moroz.bankingservice.dto.request.TransactionRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.entity.Account;
import com.moroz.bankingservice.mapper.AccountMapper;
import com.moroz.bankingservice.repository.AccountRepository;
import com.moroz.bankingservice.service.AbstractAccountService;
import com.moroz.bankingservice.service.AccountTransactionsService;
import com.moroz.bankingservice.util.BalanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Account with id %d doesn't exist".formatted(id))
                );
        final BigDecimal amountToDeposit = request.amount();
        log.info("Depositing {} to account with id {}", amountToDeposit, id);
        final BigDecimal depositResult = BalanceUtils.parseFromAccount(account).add(request.amount());
        final Account savedAccount = accountRepository.save(accountMapper.updateBalance(depositResult, account));
        log.info("Deposited {} to account with id {}", amountToDeposit, id);
        return accountMapper.toResponse(savedAccount);
    }

    @Override
    public TransactionResponse withdraw(final long id, final TransactionRequest request) {
        final Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Account with id %d doesn't exist".formatted(id))
                );
        final BigDecimal accountBalance = BalanceUtils.parseFromAccount(account);

        if (request.amount().compareTo(accountBalance) > 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Insufficient balance for account with id %d".formatted(id));
        }
        final BigDecimal amountToWithdraw = request.amount();
        log.info("Withdrawing {} from account with id {}...", amountToWithdraw, id);
        final BigDecimal withdrawResult = accountBalance.subtract(request.amount());
        final Account savedAccount = accountRepository.save(accountMapper.updateBalance(withdrawResult, account));
        log.info("Withdraw {} from account with id {} completed", amountToWithdraw, id);
        return accountMapper.toResponse(savedAccount);
    }
}
