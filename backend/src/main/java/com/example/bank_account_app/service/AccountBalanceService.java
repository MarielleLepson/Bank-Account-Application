package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.CreditBalanceDTO;
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
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountBalanceService {
    private final AccountBalanceRepository accountBalanceRepository;

    /**
     * Deposit the provided amount to the account balance.
     */
    public void depositMoney(Account account, CreditBalanceDTO dto, String createdBy) {
        log.debug("Depositing money to account: {}", account.getAccountNumber());
        List<AccountBalance> accountBalances = getAccountBalances(account);

        Currency currency = dto.getCurrency();
        BigDecimal amount = BigDecimal.valueOf(dto.getAmount());

        // Find the balance with the same currency
        AccountBalance accountBalance = accountBalances.stream()
                .filter(balance -> balance.getCurrency().equals(dto.getCurrency()))
                .findFirst()
                .orElse(null);

        // If the balance does not exist, create a new one
        if (accountBalance == null) {
            log.debug("Creating a new balance");
            accountBalance = buildAccountBalanceEntity(account, amount, currency, createdBy);
            accountBalances.add(accountBalance);
        } else {
            // Update the balance
            log.debug("Updating balance");
            accountBalance.setBalance(accountBalance.getBalance().add(amount));
            accountBalance.setLastModifiedBy(createdBy);
            accountBalance.setLastModifiedAt(LocalDateTime.now());
        }

        log.debug("Saving account balances");
        saveAllAccountBalances(accountBalances);
    }


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
