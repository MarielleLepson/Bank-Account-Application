package com.example.bank_account_app.controller;

import com.example.bank_account_app.dto.AccountDTO;
import com.example.bank_account_app.dto.CreateAccountDTO;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.service.AccountService;
import com.example.bank_account_app.util.AccountUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    /**
     * Get all bank accounts.
     */
    @Operation(summary = "Get all bank accounts", description = "Returns a list of all bank accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved bank accounts",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AccountDTO.class))))
    })
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        log.info("Fetching all bank accounts...");
        // Fetch all accounts
        List<Account> accounts = accountService.getAllAccounts();
        // Map accounts to DTO
        List<AccountDTO> response = accountService.mapAccountsToDTO(accounts);
        return ResponseEntity.ok(response);
    }

    /**
     * Get bank account by account number.
     */
    @Operation(summary = "Get bank account by account number", description = "Returns a bank account by account number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved bank account",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found - The bank account does not exist",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid account number",
                    content = @Content())
    })
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(
            @Parameter(description = "Bank account number", example = "EE123456789012345678")
            @PathVariable String accountNumber) {
        log.info("Fetching account by account number");

        // Validate account number
        if (!AccountUtils.isValidAccountNumber(accountNumber)) { // Validate account number
            log.warn("Invalid account number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account number");
        }

        // Fetch account by account number
        Account account = accountService.getAccountByAccountNumber(accountNumber);

        // Check if account exists
        if (account == null) {
            log.warn("Account not found: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        // Return account
        AccountDTO response = accountService.mapAccountToDTO(account);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a new bank account. Bank account holder name must contain only letters and spaces.
     */
    @Operation(summary = "Create a new bank account", description = "Creates a new bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created bank account",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid account holder name",
                    content = @Content())
    })
    @PostMapping("/account/create")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDTO createAccountDTO) {
        log.info("Creating a new bank account...");

        // Validate account holder name
        if (!AccountUtils.isValidAccountHolder(createAccountDTO.getAccountHolder())) {
            log.warn("Invalid account holder name: {}", createAccountDTO.getAccountHolder());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account holder name");
        }

        // Generate account number and save account
        String accountNumber = AccountUtils.generateAccountNumber();
        Account account = accountService.buildAccountEntity(accountNumber, createAccountDTO.getAccountHolder(), createAccountDTO.getAccountHolder());
        accountService.saveAccount(account);

        // Return created account
        AccountDTO response = accountService.mapAccountToDTO(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
