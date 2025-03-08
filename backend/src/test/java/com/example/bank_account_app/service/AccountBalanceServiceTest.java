package com.example.bank_account_app.service;

import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import com.example.bank_account_app.util.BalanceUtils;
import com.example.bank_account_app.util.CurrencyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountBalanceServiceTest {

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @InjectMocks
    private AccountBalanceService accountBalanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveAllAccountBalances() {
        List<AccountBalance> accountBalances = List.of(new AccountBalance(), new AccountBalance());
        when(accountBalanceRepository.saveAllAndFlush(accountBalances)).thenReturn(accountBalances);
        accountBalanceService.saveAllAccountBalances(accountBalances);
        verify(accountBalanceRepository, times(1)).saveAllAndFlush(accountBalances);
    }

    @Test
    void shouldBuildAccountBalanceEntity() {
        Account account = new Account();
        BigDecimal balance = BigDecimal.valueOf(500);
        Currency currency = Currency.EUR;
        String createdBy = "test user";

        AccountBalance accountBalance = accountBalanceService.buildAccountBalanceEntity(account, balance, currency, createdBy);

        assertNotNull(accountBalance);
        assertEquals(account, accountBalance.getAccount());
        assertEquals(balance, accountBalance.getBalance());
        assertEquals(currency, accountBalance.getCurrency());
        assertEquals(createdBy, accountBalance.getCreatedBy());
    }

    @Test
    void shouldCreateAccountBalanceWithRandomCurrency() {
        Account account = new Account();
        String createdBy = "Test User";

        // Mock the static methods to return the expected values
        try (MockedStatic<CurrencyUtils> currencyUtilsMockedStatic = mockStatic(CurrencyUtils.class);
             MockedStatic<BalanceUtils> balanceUtilsMockedStatic = mockStatic(BalanceUtils.class)) {

            currencyUtilsMockedStatic.when(CurrencyUtils::getRandomCurrency).thenReturn(Currency.EUR);
            balanceUtilsMockedStatic.when(() -> BalanceUtils.generateRandomBalance(Currency.EUR)).thenReturn(100.0);

            AccountBalance accountBalance = accountBalanceService.createAccountBalance(account, createdBy);

            assertNotNull(accountBalance);
            assertEquals(account, accountBalance.getAccount());
            assertEquals(BigDecimal.valueOf(100.0), accountBalance.getBalance());
            assertEquals(Currency.EUR, accountBalance.getCurrency());
            assertEquals(createdBy, accountBalance.getCreatedBy());
        }
    }

    @Test
    void shouldCreateAccountBalanceWithDifferentCurrency() {
        Account account = new Account();
        String createdBy = "Test User";
        List<Currency> existingCurrencies = List.of(Currency.EUR);

        // Mock the static methods to return the expected values
        try (MockedStatic<CurrencyUtils> currencyUtilsMockedStatic = mockStatic(CurrencyUtils.class);
             MockedStatic<BalanceUtils> balanceUtilsMockedStatic = mockStatic(BalanceUtils.class)) {

            currencyUtilsMockedStatic.when(CurrencyUtils::getRandomCurrency).thenReturn(Currency.EUR, Currency.USD);
            balanceUtilsMockedStatic.when(() -> BalanceUtils.generateRandomBalance(Currency.USD)).thenReturn(200.0);

            AccountBalance accountBalance = accountBalanceService.createAccountBalance(account, existingCurrencies, createdBy);

            assertNotNull(accountBalance);
            assertEquals(account, accountBalance.getAccount());
            assertEquals(BigDecimal.valueOf(200.0), accountBalance.getBalance());
            assertEquals(Currency.USD, accountBalance.getCurrency());
            assertEquals(createdBy, accountBalance.getCreatedBy());
        }
    }

    @Test
    void shouldCountAccountBalances() {
        when(accountBalanceRepository.count()).thenReturn(5L);

        int count = accountBalanceService.countAccountBalances();

        assertEquals(5, count);
        verify(accountBalanceRepository, times(1)).count();
    }
}
