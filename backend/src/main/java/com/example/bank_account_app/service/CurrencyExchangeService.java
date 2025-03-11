package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.CurrencyBalance;
import com.example.bank_account_app.dto.ExchangeCurrencyDTO;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.exceptions.BalanceNotFoundException;
import com.example.bank_account_app.exceptions.InsufficientBalanceException;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import com.example.bank_account_app.util.ExchangeRateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyExchangeService {
    private final AccountBalanceRepository accountBalanceRepository;
    private final ExternalAPIService externalAPIService;
    private final AccountBalanceService accountBalanceService;

    /**
     * Exchange currency using external API rates
     */
    public AccountBalanceDTO exchangeCurrency(ExchangeCurrencyDTO exchangeCurrencyDTO, Account account, String createdBy) {
        return processCurrencyExchange(exchangeCurrencyDTO, account, createdBy, false);
    }

    /**
     * Exchange currency using fixed rates
     */
    public AccountBalanceDTO exchangeCurrencyFixed(ExchangeCurrencyDTO exchangeCurrencyDTO, Account account, String createdBy) {
        return processCurrencyExchange(exchangeCurrencyDTO, account, createdBy, true);
    }

    /**
     * Process currency exchange (common method)
     */
    private AccountBalanceDTO processCurrencyExchange(ExchangeCurrencyDTO exchangeCurrencyDTO, Account account,
                                                      String createdBy, boolean useFixedRate) {
        log.info("Processing currency exchange: {} -> {} for amount {}",
                exchangeCurrencyDTO.getFromCurrency(), exchangeCurrencyDTO.getToCurrency(), exchangeCurrencyDTO.getAmount());

        Currency from = exchangeCurrencyDTO.getFromCurrency();
        Currency to = exchangeCurrencyDTO.getToCurrency();
        BigDecimal amount = exchangeCurrencyDTO.getAmount();

        // Validate balances
        AccountBalance fromBalance = validateAndGetBalance(account, from, amount);

        // Retrieve exchange rate
        double exchangeRate = useFixedRate ? getFixedExchangeRate(from, to) : getExternalExchangeRate(from, to);
        BigDecimal convertedAmount = amount.multiply(BigDecimal.valueOf(exchangeRate));

        // Update balances
        AccountBalance toBalance = updateBalances(account, fromBalance, to, convertedAmount, createdBy);

        log.info("Currency exchange successful: {} {} converted to {} {}", amount, from, convertedAmount, to);

        return buildAccountBalanceDTO(account, fromBalance, toBalance);
    }

    /**
     * Validate if account has enough balance in the source currency
     */
    public AccountBalance validateAndGetBalance(Account account, Currency currency, BigDecimal amount) {
        AccountBalance balance = accountBalanceRepository.findByAccountIdAndCurrency(account.getId(), currency);
        if (balance == null) {
            log.warn("Balance not found for currency: {}", currency);
            throw new BalanceNotFoundException("Account balance not found");
        }
        if (balance.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient balance: {} {} required, but only {} available", currency, amount, balance.getBalance());
            throw new InsufficientBalanceException("Insufficient balance");
        }
        return balance;
    }

    /**
     * Get exchange rate from external API
     */
    private double getExternalExchangeRate(Currency from, Currency to) {
        double exchangeRate = externalAPIService.getCurrencyExchangeRates(from.name()).getOrDefault(to.name(), 0.0);
        if (exchangeRate == 0) {
            log.warn("External exchange rate not found for {} -> {}", from, to);
            throw new BalanceNotFoundException("Exchange rate not found");
        }
        return exchangeRate;
    }

    /**
     * Get exchange rate from fixed conversion rates
     */
    private double getFixedExchangeRate(Currency from, Currency to) {
        double exchangeRate = ExchangeRateUtils.getExchangeRate(from, to);
        if (exchangeRate == 0) {
            log.warn("Fixed exchange rate not found for {} -> {}", from, to);
            throw new BalanceNotFoundException("Exchange rate not found");
        }
        return exchangeRate;
    }

    /**
     * Update the balance for both source and target currencies
     */
    private AccountBalance updateBalances(Account account, AccountBalance fromBalance, Currency to,
                                          BigDecimal convertedAmount, String createdBy) {
        fromBalance.setBalance(fromBalance.getBalance().subtract(convertedAmount));

        AccountBalance toBalance = accountBalanceRepository.findByAccountIdAndCurrency(account.getId(), to);
        if (toBalance == null) {
            toBalance = accountBalanceService.buildAccountBalanceEntity(account, convertedAmount, to, createdBy);
        } else {
            toBalance.setBalance(toBalance.getBalance().add(convertedAmount));
        }

        accountBalanceRepository.saveAll(List.of(fromBalance, toBalance));
        return toBalance;
    }

    /**
     * Build the response DTO with updated balances
     */
    private AccountBalanceDTO buildAccountBalanceDTO(Account account, AccountBalance fromBalance, AccountBalance toBalance) {
        BigDecimal fromBalanceRounded = fromBalance.getBalance().setScale(2, RoundingMode.HALF_UP);
        BigDecimal toBalanceRounded = toBalance.getBalance().setScale(2, RoundingMode.HALF_UP);

        return new AccountBalanceDTO(account.getAccountNumber(), List.of(
                new CurrencyBalance(fromBalance.getCurrency().name(), fromBalanceRounded.toString()),
                new CurrencyBalance(toBalance.getCurrency().name(), toBalanceRounded.toString())
        ));
    }
}
