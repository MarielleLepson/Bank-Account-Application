package com.example.bank_account_app.initializer;

import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import com.example.bank_account_app.repository.AccountRepository;
import com.example.bank_account_app.service.AccountBalanceService;
import com.example.bank_account_app.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class InitialDataLoaderIT {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private InitialDataLoader initialDataLoader;
    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @BeforeEach
    void setUp() {
        accountBalanceRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void shouldSkipDataLoadWhenAccountsExist() {
        Account account = new Account();
        account.setAccountNumber("EE123456789012345678");
        account.setAccountHolder("John Doe");
        account.setCreatedBy("integration test");
        accountRepository.save(account);

        initialDataLoader.run(null);

        assertTrue(initialDataLoader.alreadySetup, "Data should be marked as already loaded when accounts exist");
        assertEquals(1, accountRepository.count(), "The account should be in the database");
    }

    @Transactional
    @Test
    void shouldLoadInitialDataWhenNoAccountsExist() {
        initialDataLoader.alreadySetup = false;
        assertEquals(0, accountRepository.count(), "No accounts should exist initially.");

        initialDataLoader.run(null);

        assertTrue(initialDataLoader.alreadySetup, "Data should be marked as loaded");
        assertEquals(4, accountRepository.count(), "There should be 4 accounts created");
        assertTrue(accountBalanceService.countAccountBalances() > 0, "Account balances should be created");
    }

    @Test
    void shouldCreateAccountsCorrectly() {
        List<String> accountHolders = List.of("Mart Tamm", "Mari Maasikas", "Siim Sepp", "Kati Kask");
        List<Account> createdAccounts = initialDataLoader.createBankAccounts();
        assertEquals(accountHolders.size(), createdAccounts.size(), "The number of accounts created should match the input list size.");
        assertTrue(accountRepository.count() > 0, "Accounts should be saved to the database.");
    }

    @Test
    void shouldCreateBalancesCorrectly() {
        List<Account> accounts = List.of(
                accountService.buildAccountEntity("EE123456789012345678", "John Doe", "integration test"),
                accountService.buildAccountEntity("EE123456789012345679", "Jane Doe", "integration test")
        );
        accountRepository.saveAll(accounts);
        initialDataLoader.createAccountBalances(accounts);

        assertTrue(accountBalanceService.countAccountBalances() > 0, "Account balances should be created and saved.");
    }

    @Test
    void shouldCallAllMethods() {
        initialDataLoader.createBankAccounts();
        initialDataLoader.createAccountBalances(accountRepository.findAll());

        assertTrue(accountRepository.count() > 0, "Accounts should be created");
    }
}



