package com.moroz.bankingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moroz.bankingservice.config.JacksonConfig;
import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.AccountPageImpl;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.exception.AccountAlreadyExistsException;
import com.moroz.bankingservice.exception.AccountNotFoundException;
import com.moroz.bankingservice.service.AccountManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(JacksonConfig.class)
@WebMvcTest(AccountManagementController.class)
public class AccountManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AccountManagementService accountManagementService;

    private AccountDto account;

    @BeforeEach
    void initAccount() {
        account = AccountDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .balance(BigDecimal.valueOf(123.56))
                .build();
    }

    @Test
    void shouldReturnListOfAccounts() throws Exception {
        final AccountDto additionalAccount = AccountDto.builder()
                .id(2L)
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@yahoo.com")
                .balance(BigDecimal.valueOf(567))
                .build();

        final List<AccountDto> list = List.of(account, additionalAccount);

        final Page<AccountDto> response = new AccountPageImpl<>(list, PageRequest.of(0, 10), 2);
        when(accountManagementService.getAllAccounts(0, 10))
                .thenReturn(response);

        mockMvc.perform(get("/v0/accounts/management"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(accountManagementService, times(1)).getAllAccounts(0, 10);
    }

    @Test
    void shouldCreateAccount() throws Exception {
        final CreateAccountRequest request = new CreateAccountRequest(
                "John", "Doe", "john.doe@gmail.com", BigDecimal.valueOf(123.56)
        );
        when(accountManagementService.createAccount(request))
                .thenReturn(account);

        mockMvc.perform(
                post("/v0/accounts/management")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(account)));

        verify(accountManagementService, times(1)).createAccount(request);
    }

    @Test
    void shouldFailWhenCreatingNewAccountWithExistingEmail() throws Exception {
        final CreateAccountRequest request = new CreateAccountRequest(
                "John", "Doe", "john.doe@gmail.com", BigDecimal.valueOf(123.57)
        );

        when(accountManagementService.createAccount(request))
                .thenReturn(account);

        mockMvc.perform(
                        post("/v0/accounts/management")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(account)));

        when(accountManagementService.createAccount(request))
                .thenThrow(
                        new AccountAlreadyExistsException(request.email()));

        mockMvc.perform(
                        post("/v0/accounts/management")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertInstanceOf(AccountAlreadyExistsException.class, result.getResolvedException()))
                .andExpect(
                        result -> assertTrue(
                                result.getResolvedException().getMessage()
                                        .equals("Account with email %s already exists".formatted(request.email()))));

        verify(accountManagementService, times(2)).createAccount(request);
    }

    @Test
    void shouldReturnAccountById() throws Exception {
        when(accountManagementService.getAccountById(1L))
            .thenReturn(account);

        mockMvc.perform(get("/v0/accounts/management/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(account)));

        verify(accountManagementService, times(1)).getAccountById(1L);
    }

    @Test
    void shouldReturn404WhenGettingById() throws Exception {
        when(accountManagementService.getAccountById(2L))
                .thenThrow(new AccountNotFoundException(2L));

        mockMvc.perform(get("/v0/accounts/management/{id}", 2L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(AccountNotFoundException.class, result.getResolvedException()))
                .andExpect(
                        result ->
                                assertTrue(result.getResolvedException().getMessage().contains("Account with id 2 not found")));

        verify(accountManagementService, times(1)).getAccountById(2L);
    }
}
