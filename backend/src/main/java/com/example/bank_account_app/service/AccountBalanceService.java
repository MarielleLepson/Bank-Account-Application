package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.AccountBalanceDTO;
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
    public List<AccountBalanceDTO> mapAccountBalancesToDTO(List<AccountBalance> accountBalances) {
        List<Currency> currencies = CurrencyUtils.getSupportedCurrencies();
        return currencies.stream()
                .map(currency -> mapAccountBalanceToDTO(accountBalances, currency))
                .toList();
    }

    /**
     * Map account balance to DTO.
     */
    public AccountBalanceDTO mapAccountBalanceToDTO(List<AccountBalance> accountBalances, Currency currency) {
        AccountBalance accountBalance = accountBalances.stream()
                .filter(balance -> balance.getCurrency().equals(currency))
                .findFirst()
                .orElse(null);
        return new AccountBalanceDTO(currency.name(), accountBalance != null ? accountBalance.getBalance().toString() : "0");
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
