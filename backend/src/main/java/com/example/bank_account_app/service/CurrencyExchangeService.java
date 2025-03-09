package com.example.bank_account_app.service;

import com.example.bank_account_app.config.CurrencyExchangeConf;
import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.CurrencyBalance;
import com.example.bank_account_app.dto.ExchangeCurrencyDTO;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.exceptions.BalanceNotFoundException;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyExchangeService {
    private final AccountBalanceRepository accountBalanceRepository;
    private final ExternalAPIService externalAPIService;
    private final AccountBalanceService accountBalanceService;

    /**
     * Do the exchange for the given amount and currencies
     */

    public AccountBalanceDTO exchangeCurrency(ExchangeCurrencyDTO exchangeCurrencyDTO, Account account, String createdBy) {
        log.info("Exchange currency request: {}", exchangeCurrencyDTO);

        BigDecimal amount = exchangeCurrencyDTO.getAmount();
        Currency from = exchangeCurrencyDTO.getFromCurrency();
        Currency to = exchangeCurrencyDTO.getToCurrency();

        // Get current balance for the from currency
        AccountBalance fromBalance = accountBalanceRepository.findByAccountIdAndCurrency(
                account.getId(), exchangeCurrencyDTO.getFromCurrency());

        // Check if balance exists
        if (fromBalance == null) {
            log.warn("Account balance not found");
            throw new BalanceNotFoundException("Account balance not found");
        }

        // Check if the balance is sufficient
        if (fromBalance.getBalance().compareTo(exchangeCurrencyDTO.getAmount()) < 0) {
            log.warn("Insufficient balance");
            throw new BalanceNotFoundException("Insufficient balance");
        }

        // Get exchange rates for from currency
        double exchangeRate = externalAPIService.getCurrencyExchangeRates(String.valueOf(exchangeCurrencyDTO.getFromCurrency()))
                .get(String.valueOf(exchangeCurrencyDTO.getToCurrency()));

        // validate that the exchange rate is not null
        if (exchangeRate == 0) {
            log.warn("Exchange rate not found");
            throw new BalanceNotFoundException("Exchange rate not found");
        }

        BigDecimal convertedAmount = amount.multiply(BigDecimal.valueOf(exchangeRate));

        // Deduct from original balance
        fromBalance.setBalance(fromBalance.getBalance().subtract(amount));

        // Add to new currency balance (or create if doesn't exist)
        AccountBalance toBalance = accountBalanceRepository.findByAccountIdAndCurrency(account.getId(), to);
        if (toBalance == null) {
            toBalance = accountBalanceService.buildAccountBalanceEntity(account, convertedAmount, to, createdBy);
        }

        toBalance.setBalance(toBalance.getBalance().add(convertedAmount));

        accountBalanceRepository.saveAll(List.of(fromBalance, toBalance));

        return new AccountBalanceDTO(account.getAccountNumber(), List.of(
                new CurrencyBalance(from.name(), fromBalance.getBalance().toString()),
                new CurrencyBalance(to.name(), toBalance.getBalance().toString())
        ));
    }
}
