package com.moroz.bankingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moroz.bankingservice.config.JacksonConfig;
import com.moroz.bankingservice.dto.request.TransactionRequest;
import com.moroz.bankingservice.dto.request.TransferRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.dto.response.TransferResponse;
import com.moroz.bankingservice.service.AccountTransactionsService;
import com.moroz.bankingservice.service.AccountTransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(JacksonConfig.class)
@WebMvcTest(AccountTransactionsController.class)
public class AccountTransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AccountTransactionsService accountTransactionsService;

    @MockitoBean
    private AccountTransferService accountTransferService;

    @Test
    void shouldDeposit() throws Exception {
        final BigDecimal initAmount = BigDecimal.valueOf(100);
        final TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(123.50));
        final TransactionResponse response = new TransactionResponse(1L, initAmount.add(request.amount()));

        when(accountTransactionsService.deposit(1, request)).thenReturn(response);

        mockMvc.perform(
                patch("/v0/accounts/transactions/deposit/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))) //used manual to write request as is
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(response)));

        verify(accountTransactionsService, times(1)).deposit(anyLong(), any(TransactionRequest.class));
    }

    @Test
    void shouldWithdraw() throws Exception {
        final BigDecimal initAmount = BigDecimal.valueOf(100);
        final TransactionRequest request = new TransactionRequest(BigDecimal.valueOf(50));
        final TransactionResponse response = new TransactionResponse(1L, initAmount.subtract(request.amount()));

        when(accountTransactionsService.withdraw(1L, request)).thenReturn(response);

        mockMvc.perform(
                        patch("/v0/accounts/transactions/withdraw/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void shouldTransfer() throws Exception {
        final BigDecimal initAmountSource = BigDecimal.valueOf(100.50);
        final BigDecimal initAmountTarget = BigDecimal.valueOf(50);

        final BigDecimal amount = BigDecimal.valueOf(50.50);
        final TransferRequest request = new TransferRequest(1L, 2L, amount);
        final TransferResponse response = new TransferResponse(
                new TransactionResponse(1L, initAmountSource.subtract(amount)),
                new TransactionResponse(2L, initAmountTarget.add(amount))
        );

        when(accountTransferService.transfer(request)).thenReturn(response);

        mockMvc.perform(
                        patch("/v0/accounts/transactions/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }
}
