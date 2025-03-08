package com.example.bank_account_app.controller;

import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository accountRepo;


    /**
     * Get all bank accounts.
     */
    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts() {
        log.info("Getting all accounts");
        List<Account> accounts = accountRepo.findAll();

        if (accounts.isEmpty()) {
            log.warn("No bank accounts found");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(accounts);
    }

    /**
     * Get bank account by account number.
     */
    @PostMapping("/accounts/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(@PathVariable String accountNumber) {
        log.info("Getting account by account number: {}", accountNumber);
        Account account = accountRepo.findByAccountNumber(accountNumber);

        if (account == null) {
            log.warn("Account not found: {}", accountNumber);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }
}

