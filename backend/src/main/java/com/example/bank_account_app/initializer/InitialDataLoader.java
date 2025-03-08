package com.example.bank_account_app.initializer;

import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import com.example.bank_account_app.repository.AccountRepository;
import com.example.bank_account_app.service.AccountBalanceService;
import com.example.bank_account_app.service.AccountService;
import com.example.bank_account_app.util.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Initializes the database with sample account data upon application startup.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class InitialDataLoader implements ApplicationRunner {
    private final AccountRepository accountRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final AccountService accountService;
    private final AccountBalanceService accountBalanceService;

    private static final String DATA_LOADED_BY = "initial data loader";
    boolean alreadySetup = false;

    /**
     * Creates initial bank account data if the database is empty.
     */
    @Override
    public void run(ApplicationArguments args) {
        if (alreadySetup || accountRepository.count() > 0) {
            log.debug("Skipping initial data load (already set up).");
            alreadySetup = true;
            return;
        }
        log.info("Loading initial data...");

        List<Account> accounts = createBankAccounts();
        createAccountBalances(accounts);

        log.info("Initial data loaded successfully");
        alreadySetup = true;
    }

    /**
     * Creates sample bank account and saves them to the database.
     */
    public List<Account> createBankAccounts() {
        log.debug("Creating sample bank accounts...");
        List<String> accountHolders = List.of("Mart Tamm", "Mari Maasikas", "Siim Sepp", "Kati Kask");
        List<Account> accounts = new ArrayList<>();
        for (String name : accountHolders) {
            String accountNumber = AccountUtils.generateAccountNumber();
            Account account = accountService.buildAccountEntity(accountNumber, name, DATA_LOADED_BY);
            accounts.add(account);
        }
        accountService.saveAllAccounts(accounts);
        return accounts;
    }

    /**
     * Creates sample account balances for the given bank accounts.
     */
    public void createAccountBalances(List<Account> accounts) {
        log.debug("Creating sample account balances...");
        List<AccountBalance> accountBalances = new ArrayList<>();
        accounts.forEach(account -> {
            // Create first balance
            AccountBalance accountBalance1 = accountBalanceService.createAccountBalance(account, DATA_LOADED_BY);
            accountBalances.add(accountBalance1);

            // Create second balance with a different currency
            List<Currency> existingCurrencies = Collections.singletonList(accountBalance1.getCurrency());
            AccountBalance accountBalance2 = accountBalanceService.createAccountBalance(account, existingCurrencies, DATA_LOADED_BY);
            accountBalances.add(accountBalance2);
        });
        accountBalanceService.saveAllAccountBalances(accountBalances);
    }
}