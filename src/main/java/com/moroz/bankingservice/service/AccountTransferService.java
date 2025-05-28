package com.moroz.bankingservice.service;

import com.moroz.bankingservice.dto.request.TransferRequest;
import com.moroz.bankingservice.dto.response.TransferResponse;

public interface AccountTransferService {
    TransferResponse transfer(TransferRequest transferRequest);
}
