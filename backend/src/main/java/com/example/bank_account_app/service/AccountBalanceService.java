package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.CurrencyBalance;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import com.example.bank_account_app.util.BalanceUtils;
import com.example.bank_account_app.util.CurrencyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountBalanceService {
    private final AccountBalanceRepository accountBalanceRepository;

    /**
     * Fetches account balances for the provided accounts.
     */
    public List<AccountBalance> getAccountBalances(Account account) {
        return accountBalanceRepository.findAllByAccountId(account.getId());
    }

    /**
     * Map account balances to DTO and go though all supported currencies.
     */
    public AccountBalanceDTO mapAccountBalancesToDTO(List<AccountBalance> accountBalances, Account account) {
        List<Currency> supportedCurrencies = CurrencyUtils.getSupportedCurrencies();

        // Initialize DTO with the account number
        AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();
        accountBalanceDTO.setAccountNumber(account.getAccountNumber());

        // Map balances for all supported currencies
        List<CurrencyBalance> currencyBalances = supportedCurrencies.stream()
                .map(currency -> accountBalances.stream()
                        .filter(balance -> balance.getCurrency().equals(currency))
                        .findFirst()
                        .map(balance -> new CurrencyBalance(currency.name(), balance.getBalance().toString()))
                        .orElse(new CurrencyBalance(currency.name(), "0"))
                )
                .toList();

        accountBalanceDTO.setCurrencyBalances(currencyBalances);
        return accountBalanceDTO;
    }

    /**
     * Saves account balances for the provided accounts.
     */
    public void saveAllAccountBalances(List<AccountBalance> accountBalances) {
        accountBalanceRepository.saveAllAndFlush(accountBalances);
    }

    /**
     * Builds an account balance entity with the provided parameters.
     */
    public AccountBalance buildAccountBalanceEntity(Account account, BigDecimal balance, Currency currency, String createdBy) {
        return AccountBalance.builder()
                .account(account)
                .balance(balance)
                .currency(currency)
                .createdBy(createdBy)
                .build();
    }

    /**
     * Helper method to create an account balance with a random currency.
     */
    public AccountBalance createAccountBalance(Account account, String createdBy) {
        var currency = CurrencyUtils.getRandomCurrency();
        var balance = BalanceUtils.generateRandomBalance(currency);
        return buildAccountBalanceEntity(account, BigDecimal.valueOf(balance), currency, createdBy);
    }

    /**
     * Overloaded method to ensure the balance has a different currency. Give a list of currencies to avoid.
     */
    public AccountBalance createAccountBalance(Account account, List<Currency> existingCurrencies, String createdBy) {
        var currency = CurrencyUtils.getRandomCurrency();
        while (existingCurrencies.contains(currency)) {
            currency = CurrencyUtils.getRandomCurrency();
        }
        var balance = BalanceUtils.generateRandomBalance(currency);
        return buildAccountBalanceEntity(account, BigDecimal.valueOf(balance), currency, createdBy);
    }

    public int countAccountBalances() {
        return (int) accountBalanceRepository.count();
    }
}
