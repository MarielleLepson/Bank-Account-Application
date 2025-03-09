package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.AccountDTO;
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
     * Fetches all accounts from the database.
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Fetches an account by account number.
     */
    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Map list of accounts to DTO.
     */
    public List<AccountDTO> mapAccountsToDTO(List<Account> accounts) {
        return accounts.stream()
                .map(this::mapAccountToDTO)
                .toList();
    }

    /**
     * Map account to DTO.
     */
    public AccountDTO mapAccountToDTO(Account account) {
        return new AccountDTO().toDTO(account);
    }

    /**
     * Save account to the database.
     */
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    /**
     * Saves all accounts to the database.
     */
    public void saveAllAccounts(List<Account> accounts) {
        accountRepository.saveAllAndFlush(accounts);
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
