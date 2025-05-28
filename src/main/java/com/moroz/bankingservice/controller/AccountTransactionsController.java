package com.moroz.bankingservice.controller;

import com.moroz.bankingservice.dto.request.TransactionRequest;
import com.moroz.bankingservice.dto.request.TransferRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.dto.response.TransferResponse;
import com.moroz.bankingservice.service.AccountTransactionsService;
import com.moroz.bankingservice.service.AccountTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v0/accounts/transactions")
@RequiredArgsConstructor
public class AccountTransactionsController {
    private final AccountTransactionsService accountTransactionsService;
    private final AccountTransferService accountTransferService;

    @PatchMapping("/deposit/{id}")
    public TransactionResponse deposit(
            @PathVariable final long id, @Valid @RequestBody final TransactionRequest request
    ) {
        return accountTransactionsService.deposit(id, request);
    }

    @PatchMapping("/withdraw/{id}")
    public TransactionResponse withdraw(
            @PathVariable final long id, @Valid @RequestBody final TransactionRequest request
    ) {
        return accountTransactionsService.withdraw(id, request);
    }

    @PatchMapping("/transfer")
    public TransferResponse transfer(@Valid @RequestBody final TransferRequest request) {
        return accountTransferService.transfer(request);
    }
}
