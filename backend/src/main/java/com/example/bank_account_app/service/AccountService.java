package com.example.bank_account_app.service;

import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    /**
     * Saves all accounts to the database.
     */
    public void saveAllAccounts(List<Account> accounts) {
        accountRepository.saveAll(accounts);
    }

    /**
     * Builds an account entity.
     */
    public Account buildAccountEntity(String accountNumber, String accountHolder, String createdBy) {
        return Account.builder()
                .accountNumber(accountNumber)
                .accountHolder(accountHolder)
                .createdBy(createdBy)
                .build();
    }
}
