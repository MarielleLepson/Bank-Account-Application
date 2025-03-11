package com.example.bank_account_app.integration.initializer;

import com.example.bank_account_app.initializer.InitialDataLoader;
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

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class InitialDataLoaderIntegrationTest {

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
        account.setAccountHolder("Mari Maasikas");
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
}



