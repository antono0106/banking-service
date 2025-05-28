package com.moroz.bankingservice.repository;

import com.moroz.bankingservice.entity.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void initAccountAndSave() {
        account = new Account();
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setEmail("john@doe.com");
        account.setBalance(1000);
        account.setCents(0);
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
        newAccount.setCents(50);
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
        assertEquals(0, newAccount.getBalance());
        assertEquals(0, newAccount.getCents());
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
        assertEquals(10, savedAccount.getBalance());
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

    /*@Test
    void shouldNotUpdatedWithNegativeBalance() {
        final List<Account> accounts = accountRepository.findAll();
        assertEquals(1, accounts.size());

        final Account account = accounts.stream().findFirst().get();
        // H2 doesn't support check constraints, so balance setters explicitly check if balance is in correct range
        assertThrows(IllegalArgumentException.class, () -> account.setBalance(-1));
        assertThrows(IllegalArgumentException.class, () -> account.setCents(-1));
    }*/

    @Test
    void shouldExistByEmail() {
        assertTrue(accountRepository.existsByEmail(account.getEmail()));
    }

    @Test
    void shouldNotExistByEmail() {
        assertFalse(accountRepository.existsByEmail("dummy@doe.com"));
    }
}
