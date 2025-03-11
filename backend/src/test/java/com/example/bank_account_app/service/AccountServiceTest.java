package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.AccountDTO;
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
    void shouldSaveAccount() {
        Account account = new Account();
        when(accountRepository.save(account)).thenReturn(account);

        accountService.saveAccount(account);

        verify(accountRepository, times(1)).save(account);
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

    @Test
    void shouldMapAccountToDTO() {
        Account account = new Account();
        account.setAccountNumber("EE123456789012345678");
        account.setAccountHolder("John Doe");
        account.setCreatedBy("test user");

        AccountDTO accountDTO = accountService.mapAccountToDTO(account);

        assertNotNull(accountDTO, "Account DTO should not be null");
        assertEquals(account.getAccountNumber(), accountDTO.getAccountNumber(), "Account number should match");
        assertEquals(account.getAccountHolder(), accountDTO.getAccountHolder(), "Account holder should match");
    }

    @Test
    void shouldMapAccountsToDTO() {
        List<Account> accounts = List.of(new Account(), new Account());

        List<AccountDTO> accountDTOs = accountService.mapAccountsToDTO(accounts);

        assertNotNull(accountDTOs, "Account DTOs should not be null");
        assertEquals(accounts.size(), accountDTOs.size(), "Account DTOs size should match");
    }

    @Test
    void shouldGetAccountByAccountNumber() {
        String accountNumber = "EE123456789012345678";
        Account account = new Account();
        account.setAccountNumber(accountNumber);

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(account);

        Account result = accountService.getAccountByAccountNumber(accountNumber);

        assertNotNull(result, "Account should not be null");
        assertEquals(accountNumber, result.getAccountNumber(), "Account number should match");
    }

    @Test
    void shouldGetAllAccounts() {
        List<Account> accounts = List.of(new Account(), new Account());

        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result, "Accounts should not be null");
        assertEquals(accounts.size(), result.size(), "Accounts size should match");
    }
}

