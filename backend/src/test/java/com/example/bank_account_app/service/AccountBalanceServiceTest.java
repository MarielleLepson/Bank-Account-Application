package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.CreditBalanceDTO;
import com.example.bank_account_app.dto.DebitBalanceDTO;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.exceptions.InsufficientBalanceException;
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
import java.time.LocalDateTime;
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

    @Test
    void shouldTestCreatingAccountBalanceDTO() {
        Account account = new Account();

        AccountBalance accountBalance = AccountBalance.builder()
            .account(account)
            .balance(BigDecimal.valueOf(100))
            .currency(Currency.EUR)
            .createdAt(LocalDateTime.now())
            .createdBy("test user")
            .build();

        AccountBalance accountBalance2 = AccountBalance.builder()
            .account(account)
            .balance(BigDecimal.valueOf(200))
            .currency(Currency.USD)
            .createdAt(LocalDateTime.now())
            .createdBy("test user")
            .build();

        List<AccountBalance> accountBalances = List.of(
            accountBalance,
            accountBalance2
        );
        AccountBalanceDTO accountBalanceDTO = accountBalanceService.mapAccountBalancesToDTO(accountBalances, account);

        assertNotNull(accountBalanceDTO);
        assertEquals(5, accountBalanceDTO.getCurrencyBalances().size());
        assertEquals(account.getAccountNumber(), accountBalanceDTO.getAccountNumber());
        assertEquals("EUR", accountBalanceDTO.getCurrencyBalances().get(0).getCurrency());
        assertEquals("100", accountBalanceDTO.getCurrencyBalances().get(0).getBalance());
        assertEquals("USD", accountBalanceDTO.getCurrencyBalances().get(1).getCurrency());
        assertEquals("200", accountBalanceDTO.getCurrencyBalances().get(1).getBalance());
    }


    @Test
    void shouldTestGettingAccountBalances() {
        Account account = new Account();
        AccountBalance accountBalance = AccountBalance.builder()
            .account(account)
            .balance(BigDecimal.valueOf(100))
            .currency(Currency.EUR)
            .createdAt(LocalDateTime.now())
            .createdBy("test user")
            .build();

        AccountBalance accountBalance2 = AccountBalance.builder()
            .account(account)
            .balance(BigDecimal.valueOf(200))
            .currency(Currency.USD)
            .createdAt(LocalDateTime.now())
            .createdBy("test user")
            .build();

        List<AccountBalance> accountBalances = List.of(
            accountBalance,
            accountBalance2
        );

        when(accountBalanceRepository.findAllByAccountId(account.getId())).thenReturn(accountBalances);

        List<AccountBalance> accountBalancesResult = accountBalanceService.getAccountBalances(account);

        assertNotNull(accountBalancesResult);
        assertEquals(2, accountBalancesResult.size());
        assertEquals(accountBalance, accountBalancesResult.get(0));
        assertEquals(accountBalance2, accountBalancesResult.get(1));
    }

    @Test
    void shouldTestIsBalanceSufficient() {
        AccountBalance accountBalance = AccountBalance.builder()
            .balance(BigDecimal.valueOf(100))
            .build();

        assertTrue(accountBalanceService.isBalanceSufficient(BigDecimal.valueOf(50), accountBalance));
        assertFalse(accountBalanceService.isBalanceSufficient(BigDecimal.valueOf(150), accountBalance));
    }

    @Test
    void shouldTestIsBalanceSufficientWithZeroBalance() {
        AccountBalance accountBalance = AccountBalance.builder()
            .balance(BigDecimal.ZERO)
            .build();

        assertFalse(accountBalanceService.isBalanceSufficient(BigDecimal.valueOf(50), accountBalance));
    }

    @Test
    void shouldTestIsBalanceSufficientWithNegativeBalance() {
        AccountBalance accountBalance = AccountBalance.builder()
            .balance(BigDecimal.valueOf(-50))
            .build();

        assertFalse(accountBalanceService.isBalanceSufficient(BigDecimal.valueOf(50), accountBalance));
    }

    // test debit money
    @Test
    void shouldTestDebitMoney() {
        Account account = new Account();
        AccountBalance accountBalance = AccountBalance.builder()
            .account(account)
            .balance(BigDecimal.valueOf(100))
            .currency(Currency.EUR)
            .createdAt(LocalDateTime.now())
            .createdBy("test user")
            .build();

        List<AccountBalance> accountBalances = List.of(accountBalance);

        when(accountBalanceRepository.findAllByAccountId(account.getId())).thenReturn(accountBalances);

        accountBalanceService.debitMoney(account, new DebitBalanceDTO("Mari Maasikas",Currency.EUR, 50), "test user");

        verify(accountBalanceRepository, times(1)).findAllByAccountId(account.getId());
        verify(accountBalanceRepository, times(1)).saveAllAndFlush(accountBalances);
        assertEquals(BigDecimal.valueOf(50.0), accountBalance.getBalance());
    }

    @Test
    void shouldTestDebitMoneyWithInsufficientBalance() {
        Account account = new Account();
        AccountBalance accountBalance = AccountBalance.builder()
            .account(account)
            .balance(BigDecimal.valueOf(100))
            .currency(Currency.EUR)
            .createdAt(LocalDateTime.now())
            .createdBy("test user")
            .build();

        List<AccountBalance> accountBalances = List.of(accountBalance);

        when(accountBalanceRepository.findAllByAccountId(account.getId())).thenReturn(accountBalances);

        assertThrows(InsufficientBalanceException.class, () -> {
            accountBalanceService.debitMoney(account, new DebitBalanceDTO("Mari Maasikas",Currency.EUR, 150), "test user");
        });

        verify(accountBalanceRepository, times(1)).findAllByAccountId(account.getId());
        verify(accountBalanceRepository, times(0)).saveAllAndFlush(accountBalances);
        assertEquals(BigDecimal.valueOf(100), accountBalance.getBalance());
    }


    @Test
    void shouldTestCreditMoney() {
        Account account = new Account();
        AccountBalance accountBalance = AccountBalance.builder()
            .account(account)
            .balance(BigDecimal.valueOf(100))
            .currency(Currency.EUR)
            .createdAt(LocalDateTime.now())
            .createdBy("test user")
            .build();

        List<AccountBalance> accountBalances = List.of(accountBalance);

        when(accountBalanceRepository.findAllByAccountId(account.getId())).thenReturn(accountBalances);

        accountBalanceService.creditMoney(account, new CreditBalanceDTO("Mari Maasikas",Currency.EUR, 50), "test user");

        verify(accountBalanceRepository, times(1)).findAllByAccountId(account.getId());
        verify(accountBalanceRepository, times(1)).saveAllAndFlush(accountBalances);
        assertEquals(BigDecimal.valueOf(150.0), accountBalance.getBalance());
    }
}
