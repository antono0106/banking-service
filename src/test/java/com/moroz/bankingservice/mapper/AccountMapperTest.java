package com.moroz.bankingservice.mapper;

import com.moroz.bankingservice.dto.AccountDto;
import com.moroz.bankingservice.dto.request.CreateAccountRequest;
import com.moroz.bankingservice.dto.response.TransactionResponse;
import com.moroz.bankingservice.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountMapperTest {

    private static final AccountMapper MAPPER = Mappers.getMapper(AccountMapper.class);

    private final Account account = new Account();

    @BeforeEach
    void initAccount() {
        account.setId(1L);
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setEmail("john@doe.com");
        account.setBalance(100);
    }

    @Test
    void shouldConvertToDto() {
        final AccountDto dto = MAPPER.toDto(account);
        assertNotNull(dto);
        assertEquals(dto.id(), account.getId());
        assertEquals(dto.firstName(), account.getFirstName());
        assertEquals(dto.lastName(), account.getLastName());
        assertEquals(dto.email(), account.getEmail());
        assertEquals(dto.balance(), account.getBalance());
    }

    @Test
    void shouldCreateAccount() {
        final CreateAccountRequest request = new CreateAccountRequest(
                "John", "Doe", "john@doe.com", BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP)
        );
        final Account result = MAPPER.fromRequest(request);
        assertNotNull(result);
        assertEquals(result.getFirstName(), account.getFirstName());
        assertEquals(result.getLastName(), account.getLastName());
        assertEquals(result.getEmail(), account.getEmail());
        assertEquals(result.getBalance(), account.getBalance());
    }

    @Test
    void shouldUpdateBalance() {
        final BigDecimal amount = BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);
        MAPPER.updateBalance(amount, account);

        assertNotNull(account);
        assertEquals(amount, account.getBalance());
    }

    @Test
    void shouldConvertToTransactionResponse() {
        final TransactionResponse response = MAPPER.toResponse(account);
        assertNotNull(response);
        assertEquals(response.id(), account.getId());
        assertEquals(response.currentBalance(), account.getBalance());
    }
}
