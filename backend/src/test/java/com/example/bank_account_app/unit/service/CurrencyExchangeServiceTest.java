package com.example.bank_account_app.unit.service;

import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.exceptions.BalanceNotFoundException;
import com.example.bank_account_app.exceptions.InsufficientBalanceException;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import com.example.bank_account_app.service.AccountBalanceService;
import com.example.bank_account_app.service.CurrencyExchangeService;
import com.example.bank_account_app.service.ExternalAPIService;
import com.example.bank_account_app.util.ExchangeRateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeServiceTest {

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Mock
    private ExternalAPIService externalAPIService;

    @Mock
    private AccountBalanceService accountBalanceService;

    @InjectMocks
    private CurrencyExchangeService currencyExchangeService;

    private Method getExternalExchangeRateMethod;
    private Method getFixedExchangeRateMethod;
    private Method updateBalancesMethod;

    @BeforeEach
    void setUp() throws Exception {
        getExternalExchangeRateMethod = CurrencyExchangeService.class
                .getDeclaredMethod("getExternalExchangeRate", Currency.class, Currency.class);
        getExternalExchangeRateMethod.setAccessible(true);

        getFixedExchangeRateMethod = CurrencyExchangeService.class
                .getDeclaredMethod("getFixedExchangeRate", Currency.class, Currency.class);
        getFixedExchangeRateMethod.setAccessible(true);

        updateBalancesMethod = CurrencyExchangeService.class
                .getDeclaredMethod("updateBalances", Account.class, AccountBalance.class, Currency.class, BigDecimal.class, String.class);
        updateBalancesMethod.setAccessible(true);
    }


    @Test
    void testGetExternalExchangeRate_Success() throws Exception {
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        when(externalAPIService.getCurrencyExchangeRates("USD")).thenReturn(rates);
        double result = (double) getExternalExchangeRateMethod.invoke(currencyExchangeService, Currency.USD, Currency.EUR);
        assertEquals(0.85, result, 0.0001);
    }

    @Test
    void testGetExternalExchangeRate_RateNotFound() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.0);
        when(externalAPIService.getCurrencyExchangeRates("USD")).thenReturn(rates);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () ->
                getExternalExchangeRateMethod.invoke(currencyExchangeService, Currency.USD, Currency.EUR));
        assertTrue(exception.getCause() instanceof BalanceNotFoundException);
    }


    @Test
    void testGetFixedExchangeRate_Success() throws Exception {
        try (MockedStatic<ExchangeRateUtils> mockedStatic = mockStatic(ExchangeRateUtils.class)) {
            mockedStatic.when(() -> ExchangeRateUtils.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(1.2);
            double result = (double) getFixedExchangeRateMethod.invoke(currencyExchangeService, Currency.USD, Currency.EUR);
            assertEquals(1.2, result, 0.0001);
        }
    }

    @Test
    void testGetFixedExchangeRate_RateNotFound() {
        try (MockedStatic<ExchangeRateUtils> mockedStatic = mockStatic(ExchangeRateUtils.class)) {
            mockedStatic.when(() -> ExchangeRateUtils.getExchangeRate(Currency.USD, Currency.EUR)).thenReturn(0.0);
            InvocationTargetException exception = assertThrows(InvocationTargetException.class, () ->
                    getFixedExchangeRateMethod.invoke(currencyExchangeService, Currency.USD, Currency.EUR));
            assertInstanceOf(BalanceNotFoundException.class, exception.getCause());
        }
    }

    @Test
    void testUpdateBalances_ExistingTarget() throws Exception {
        Account account = new Account();

        AccountBalance fromBalance = new AccountBalance();
        fromBalance.setCurrency(Currency.USD);
        fromBalance.setBalance(new BigDecimal("200"));

        AccountBalance toBalance = new AccountBalance();
        toBalance.setCurrency(Currency.EUR);
        toBalance.setBalance(new BigDecimal("50"));

        when(accountBalanceRepository.findByAccountIdAndCurrency(account.getId(), Currency.EUR)).thenReturn(toBalance);

        BigDecimal convertedAmount = new BigDecimal("40");
        AccountBalance result = (AccountBalance) updateBalancesMethod.invoke(currencyExchangeService, account, fromBalance, Currency.EUR, convertedAmount, "tester");

        assertEquals(new BigDecimal("160"), fromBalance.getBalance());
        assertEquals(new BigDecimal("90"), toBalance.getBalance());
        assertSame(toBalance, result);
    }

    @Test
    void testUpdateBalances_NewTarget() throws Exception {
        Account account = new Account();

        AccountBalance fromBalance = new AccountBalance();
        fromBalance.setCurrency(Currency.USD);
        fromBalance.setBalance(new BigDecimal("200"));

        when(accountBalanceRepository.findByAccountIdAndCurrency(account.getId(), Currency.EUR)).thenReturn(null);
        BigDecimal convertedAmount = new BigDecimal("40");

        AccountBalance newToBalance = new AccountBalance();
        newToBalance.setCurrency(Currency.EUR);
        newToBalance.setBalance(convertedAmount);
        when(accountBalanceService.buildAccountBalanceEntity(account, convertedAmount, Currency.EUR, "tester"))
                .thenReturn(newToBalance);

        AccountBalance result = (AccountBalance) updateBalancesMethod.invoke(currencyExchangeService, account, fromBalance, Currency.EUR, convertedAmount, "tester");
        assertEquals(new BigDecimal("160"), fromBalance.getBalance());
        assertEquals(new BigDecimal("40"), newToBalance.getBalance());
        assertSame(newToBalance, result);
    }

    @Test
    void testValidateAndGetBalance_InsufficientBalance() {
        Account account = new Account();

        AccountBalance balance = new AccountBalance();
        balance.setBalance(new BigDecimal("50"));

        when(accountBalanceRepository.findByAccountIdAndCurrency(account.getId(), Currency.USD))
                .thenReturn(balance);
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () ->
                currencyExchangeService.validateAndGetBalance(account, Currency.USD, new BigDecimal("100")));
        assertInstanceOf(InsufficientBalanceException.class, exception);
        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    void testValidateAndGetBalance_BalanceNotFound() {
        Account account = new Account();
        when(accountBalanceRepository.findByAccountIdAndCurrency(account.getId(), Currency.USD))
                .thenReturn(null);
        BalanceNotFoundException exception = assertThrows(BalanceNotFoundException.class, () ->
                currencyExchangeService.validateAndGetBalance(account, Currency.USD, new BigDecimal("100")));
        assertInstanceOf(BalanceNotFoundException.class, exception);
        assertEquals("Account balance not found", exception.getMessage());
    }
}
