package com.example.bank_account_app.service;

import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveAllAccounts() {
        List<Account> accounts = List.of(new Account(), new Account());
        when(accountRepository.saveAllAndFlush(accounts)).thenReturn(accounts);

        accountService.saveAllAccounts(accounts);

        verify(accountRepository, times(1)).saveAllAndFlush(accounts);
    }


    @Test
    void shouldBuildAccountEntity() {
        String accountNumber = "EE123456789012345678";
        String accountHolder = "John Doe";
        String createdBy = "test user";

        Account account = accountService.buildAccountEntity(accountNumber, accountHolder, createdBy);

        assertNotNull(account, "Account should not be null");
        assertEquals(accountNumber, account.getAccountNumber(), "Account number should match");
        assertEquals(accountHolder, account.getAccountHolder(), "Account holder should match");
        assertEquals(createdBy, account.getCreatedBy(), "Created by should match");
    }
}

