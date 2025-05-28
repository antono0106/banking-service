package com.moroz.bankingservice.controller;

import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.service.AccountManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v0/accounts/management")
@RequiredArgsConstructor
public class AccountManagementController {
    private final AccountManagementService accountManagementService;

    @GetMapping
    public Page<AccountDto> getAccounts(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        return accountManagementService.getAllAccounts(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto createAccount(@Valid @RequestBody final CreateAccountRequest request) {
        return accountManagementService.createAccount(request);
    }

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable final long id) {
        return accountManagementService.getAccountById(id);
    }
}
