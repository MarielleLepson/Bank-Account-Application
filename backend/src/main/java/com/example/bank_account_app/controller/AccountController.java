package com.example.bank_account_app.controller;

import com.example.bank_account_app.dto.AccountByAccountNumberDTO;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get all bank accounts", description = "Returns a list of all bank accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all bank accounts"),
            @ApiResponse(responseCode = "404", description = "Not found - No bank accounts found")
    })
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
    @Operation(summary = "Get bank account by account number", description = "Returns a bank account by account number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved bank account by account number"),
            @ApiResponse(responseCode = "404", description = "Not found - The bank account does not exist")
    })
    @PostMapping("/accounts/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(
            @Parameter(description = "Bank account number", example = "EE123456789012345678")
            @PathVariable String accountNumber) {
        log.info("Getting account by account number: {}", accountNumber);
        Account account = accountRepo.findByAccountNumber(accountNumber);

        if (account == null) {
            log.warn("Account not found: {}", accountNumber);
            return ResponseEntity.notFound().build();
        }

        AccountByAccountNumberDTO accountDTO = new AccountByAccountNumberDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountHolder(),
                account.getCreatedAt(),
                account.getCreatedBy()
        );
        return ResponseEntity.ok(accountDTO);
    }
}

