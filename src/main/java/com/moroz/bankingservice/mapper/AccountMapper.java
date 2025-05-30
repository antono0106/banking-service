package com.moroz.bankingservice.mapper;

import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.entity.Account;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AccountMapper {

    AccountDto toDto(Account account);

    @Mapping(target = "balance", source = "request.initBalance")
    Account fromRequest(CreateAccountRequest request);

    @Mapping(target = "balance", source = "amount")
    void updateBalance(BigDecimal amount, @MappingTarget Account account);

    @Mapping(target = "currentBalance", source = "balance")
    TransactionResponse toResponse(Account account);
}
