package com.moroz.bankingservice.repository;

import com.moroz.bankingservice.entity.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("bank")
            .withUsername("postgres")
            .withPassword("postgres")
            .withExposedPorts(5432);

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @DynamicPropertySource
    static void initProps(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", CONTAINER::getUsername);
        registry.add("spring.datasource.password", CONTAINER::getPassword);
        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.liquibase.change-log", () -> "classpath:/db/changelog/db.changelog-master.xml");
    }

    @BeforeEach
    void initAccountAndSave() {
        account = new Account();
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setEmail("john@doe.com");
        account.setBalance(1000);
        accountRepository.save(account);
    }

    @AfterEach
    void truncateTable() {
        accountRepository.deleteAll();
    }

    @Test
    void shouldSaveNewAccount() {
        final Account newAccount = new Account();
        newAccount.setFirstName("John");
        newAccount.setLastName("Doe");
        newAccount.setEmail("john2@doe.com");
        newAccount.setBalance(1050);
        final Account savedAccount = accountRepository.save(newAccount);

        assertNotNull(savedAccount);
        assertEquals(2, accountRepository.count());
    }

    @Test
    void shouldSaveNewAccountWithEmptyBalance() {
        final Account newAccount = new Account();
        newAccount.setFirstName("John");
        newAccount.setLastName("Doe");
        newAccount.setEmail("john2@doe.com");

        final Account savedAccount = accountRepository.save(newAccount);

        assertNotNull(savedAccount);
        assertEquals(2, accountRepository.count());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), newAccount.getBalance());
    }

    @Test
    void shouldUpdateAccount() {
        final List<Account> accounts = accountRepository.findAll();
        assertEquals(1, accounts.size());

        final Account account = accounts.stream().findFirst().get();

        final String nameToUpdate = "Johny";
        account.setFirstName(nameToUpdate);
        final String lastNameToUpdate = "Notdoe";
        account.setLastName(lastNameToUpdate);
        final String emailToUpdate = "john2@doe.com";
        account.setEmail(emailToUpdate);
        account.setBalance(10);

        final Account savedAccount = accountRepository.save(account);
        assertNotNull(savedAccount);
        assertEquals(nameToUpdate, savedAccount.getFirstName());
        assertEquals(lastNameToUpdate, savedAccount.getLastName());
        assertEquals(emailToUpdate, savedAccount.getEmail());
        assertEquals(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP), savedAccount.getBalance());
        assertEquals(1, accountRepository.count());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // added in order to catch exception so the transaction couldn't
    void shouldNotUpdateWithNull() {
        final List<Account> accounts = accountRepository.findAll();
        assertEquals(1, accounts.size());

        final Account account = accounts.stream().findFirst().get();
        account.setEmail(null);

        assertThrows(DataIntegrityViolationException.class, () -> accountRepository.save(account));
        assertEquals(1, accountRepository.count());
        assertNotNull(accountRepository.findAll().stream().findFirst().get().getEmail());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldNotUpdateWithNegativeBalance() {
        final List<Account> accounts = accountRepository.findAll();
        assertEquals(1, accounts.size());

        final Account account = accounts.stream().findFirst().get();
        account.setBalance(-1);

        assertThrows(DataIntegrityViolationException.class, () -> accountRepository.save(account));
        assertEquals(1, accountRepository.count());
        assertEquals(
                BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP),
                accountRepository.findAll().stream().findFirst().get().getBalance()
        );
    }

    @Test
    void shouldFindById() {
        assertEquals(Optional.of(account), accountRepository.findById(account.getId()));
    }

    @Test
    void shouldNotExistsById() {
        assertEquals(Optional.empty(), accountRepository.findById(1L));
    }

    @Test
    void shouldExistByEmail() {
        assertTrue(accountRepository.existsByEmail(account.getEmail()));
    }

    @Test
    void shouldNotExistByEmail() {
        assertFalse(accountRepository.existsByEmail("dummy@doe.com"));
    }
}
