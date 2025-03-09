package com.example.bank_account_app.controller;

import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.service.AccountBalanceService;
import com.example.bank_account_app.service.AccountService;
import com.example.bank_account_app.util.AccountUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/account-balance")
@RequiredArgsConstructor
public class AccountBalanceController {
    private final AccountBalanceService accountBalanceService;
    private final AccountService accountService;

    /**
     * Feature 3: Get account balances by account number. Retrieve the account balance for all supported currencies.
     */
    @Operation(summary = "Feature 3: Get account balances by account number", description = "Returns the account balance for all supported currencies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account balance",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountBalanceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found - The account does not exist",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid account number",
                    content = @Content())
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getAccountBalance(@Parameter(description = "Bank account number", example = "EE123456789012345678")
                                               @PathVariable String accountNumber) {
        // Validate account number
        if (!AccountUtils.isValidAccountNumber(accountNumber)) {
            log.warn("Invalid account number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid account number");
        }

        // Fetch account by account number
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (account == null) {
            log.warn("Account not found for account number: {}", accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        // Fetch account balances for all supported currencies
        List<AccountBalance> accountBalances = accountBalanceService.getAccountBalances(account);

        // Map account balances to DTO
        AccountBalanceDTO response = accountBalanceService.mapAccountBalancesToDTO(accountBalances, account);
        return ResponseEntity.ok(response);
    }

}
