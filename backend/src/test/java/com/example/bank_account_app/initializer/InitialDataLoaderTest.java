package com.example.bank_account_app.initializer;

import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.repository.AccountRepository;
import com.example.bank_account_app.service.AccountBalanceService;
import com.example.bank_account_app.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ApplicationArguments;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InitialDataLoaderTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountBalanceService accountBalanceService;

    @InjectMocks
    private InitialDataLoader initialDataLoader;

    @Mock
    private ApplicationArguments applicationArguments;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSkipDataLoadWhenAccountsExist() {
        when(accountRepository.count()).thenReturn(1L);

        initialDataLoader.run(applicationArguments);

        verify(accountBalanceService, never()).saveAllAccountBalances(anyList());
        verify(accountService, never()).saveAllAccounts(anyList());
        assertTrue(initialDataLoader.alreadySetup, "Data should be marked as already loaded when accounts exist");
    }

    @Test
    void shouldLoadInitialDataWhenNoAccountsExist() {
        when(accountRepository.count()).thenReturn(0L);
        when(accountService.buildAccountEntity(anyString(), anyString(), anyString())).thenReturn(new Account());

        doNothing().when(accountService).saveAllAccounts(anyList());

        AccountBalance mockBalance = mock(AccountBalance.class);
        when(accountBalanceService.createAccountBalance(any(), anyString())).thenReturn(mockBalance);
        when(accountBalanceService.createAccountBalance(any(), any(), anyString())).thenReturn(mockBalance);
        doNothing().when(accountBalanceService).saveAllAccountBalances(anyList());

        initialDataLoader.run(applicationArguments);

        verify(accountService).saveAllAccounts(anyList());
        verify(accountBalanceService).saveAllAccountBalances(anyList());
        verify(accountBalanceService, times(4)).createAccountBalance(any(), anyString());
        verify(accountBalanceService, times(4)).createAccountBalance(any(), any(), anyString());

        assertTrue(initialDataLoader.alreadySetup, "Data should be marked as loaded");
    }

    @Test
    void shouldCreateAccountsCorrectly() {
        List<String> accountHolders = List.of("Mart Tamm", "Mari Maasikas", "Siim Sepp", "Kati Kask");
        List<Account> accounts = new ArrayList<>();
        for (String holder : accountHolders) {
            accounts.add(new Account());
        }
        when(accountService.buildAccountEntity(anyString(), anyString(), anyString())).thenReturn(new Account());
        doNothing().when(accountService).saveAllAccounts(anyList());

        List<Account> createdAccounts = initialDataLoader.createBankAccounts();

        assertEquals(accountHolders.size(), createdAccounts.size());
        verify(accountService, times(accountHolders.size())).buildAccountEntity(anyString(), anyString(), anyString());
        verify(accountService, times(1)).saveAllAccounts(anyList());
    }

    @Test
    void shouldCreateBalancesCorrectly() {
        List<Account> accounts = List.of(new Account(), new Account());
        AccountBalance mockedAccountBalance = mock(AccountBalance.class);
        when(accountBalanceService.createAccountBalance(any(), anyString())).thenReturn(mockedAccountBalance);
        when(accountBalanceService.createAccountBalance(any(), any(), anyString())).thenReturn(mockedAccountBalance);
        doNothing().when(accountBalanceService).saveAllAccountBalances(anyList());

        initialDataLoader.createAccountBalances(accounts);

        verify(accountBalanceService, times(2)).createAccountBalance(any(), anyString());  // Should be called twice for each account
        verify(accountBalanceService, times(2)).createAccountBalance(any(), any(), anyString());  // Ensure second balance is created with a different currency
        verify(accountBalanceService, times(1)).saveAllAccountBalances(anyList());  // Ensure all balances are saved
    }
}


