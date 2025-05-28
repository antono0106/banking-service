package com.moroz.bankingservice.service;

import com.moroz.bankingservice.dto.request.TransactionRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;

public interface AccountTransactionsService {

    TransactionResponse deposit(long id, TransactionRequest request);

    TransactionResponse withdraw(long id, TransactionRequest request);
}
