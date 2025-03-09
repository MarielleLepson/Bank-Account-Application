package com.example.bank_account_app.controller;

import com.example.bank_account_app.dto.AccountDTO;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.repository.AccountRepository;
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
@RequestMapping("/api/accounts")
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
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        log.info("Fetching all bank accounts...");
        List<Account> accounts = accountService.getAllAccounts();
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
    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(
            @Parameter(description = "Bank account number", example = "EE123456789012345678")
            @PathVariable String accountNumber) {
        log.info("Fetching account with account number: {}", accountNumber);

        if (!AccountUtils.isValidAccountNumber(accountNumber)) { // Validate account number
            log.warn("Invalid account number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account number");
        }

        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (account == null) {
            log.warn("Account not found: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        AccountDTO response = accountService.mapAccountToDTO(account);
        return ResponseEntity.ok(response);
    }
}
