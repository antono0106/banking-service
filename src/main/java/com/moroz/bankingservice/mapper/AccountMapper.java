package com.moroz.bankingservice.mapper;

import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "balance",
            expression = "java(com.moroz.bankingservice.util.BalanceUtils.parseFromAccount(account))")
    AccountDto toDto(Account account);

    @Mapping(target = "balance",
            expression = "java(com.moroz.bankingservice.util.BalanceUtils.getBalanceFromAmount(request.initBalance()))")
    @Mapping(target = "cents",
            expression = "java(com.moroz.bankingservice.util.BalanceUtils.getCentsFromAmount(request.initBalance()))")
    Account fromRequest(CreateAccountRequest request);

    @Mapping(target = "balance",
            expression = "java(com.moroz.bankingservice.util.BalanceUtils.getBalanceFromAmount(amount))")
    @Mapping(target = "cents",
            expression = "java(com.moroz.bankingservice.util.BalanceUtils.getCentsFromAmount(amount))")
    Account updateBalance(BigDecimal amount, @MappingTarget Account account);

    @Mapping(target = "currentBalance",
            expression = "java(com.moroz.bankingservice.util.BalanceUtils.parseFromAccount(account))")
    TransactionResponse toResponse(Account account);
}
