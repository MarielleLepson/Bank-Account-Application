package com.example.bank_account_app.controller;

import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.CreditBalanceDTO;
import com.example.bank_account_app.dto.DebitBalanceDTO;
import com.example.bank_account_app.dto.TransactionCommand;
import com.example.bank_account_app.enums.TransactionType;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.service.AccountBalanceService;
import com.example.bank_account_app.service.AccountService;
import com.example.bank_account_app.service.TransactionService;
import com.example.bank_account_app.util.AccountUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/account-balance")
@RequiredArgsConstructor
public class AccountBalanceController {
    private final AccountBalanceService accountBalanceService;
    private final AccountService accountService;
    private final TransactionService transactionService;

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

        // Create response DTO
        AccountBalanceDTO response = accountBalanceService.mapAccountBalancesToDTO(accountBalances, account);
        return ResponseEntity.ok(response);
    }

    /**
     * Feature 1: Add Money to Account. Enable depositing money into an account for a specific currency.
     */
    @Operation(summary = "Feature 1: Add Money to Account", description = "Deposit money into an account for a specific currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deposited money",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found - The account does not exist",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid CreditBalanceDTO payload",
                    content = @Content())
    })
    @PostMapping("/credit")
    public ResponseEntity<?> depositMoney(
            @Valid @RequestBody CreditBalanceDTO creditBalanceDTO,
            Errors errors) {

        log.info("Depositing money into account...");
        // Validate request DTO
        if (errors.hasErrors()) {
            log.warn("Invalid request: {}", errors.getAllErrors());
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        // Fetch account by account number
        Account account = accountService.getAccountByAccountNumber(creditBalanceDTO.getAccountNumber());
        if (account == null) {
            log.warn("Account not found for account number: {}", creditBalanceDTO.getAccountNumber());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        // Deposit money into account for the specified currency
        accountBalanceService.creditMoney(account, creditBalanceDTO, account.getAccountHolder());

        // Also save this step as a transaction
        TransactionCommand cmd = TransactionCommand.builder()
                .account(account)
                .amount(creditBalanceDTO.getAmount())
                .currency(creditBalanceDTO.getCurrency())
                .type(TransactionType.CREDIT)
                .createdBy(account.getAccountHolder())
                .build();
        transactionService.createNewTransaction(cmd);

        log.info("Deposit successful");
        return ResponseEntity.ok("Deposit successful");
    }


    /**
     * Feature 2: Debit Money from Account. Allow debiting money from an account in a single currency. Automatic currency exchange is not allowed for debiting operations.
     * The account must have sufficient balance in the specified currency.
     */
    @Operation(summary = "Feature 2: Withdraw money from Account", description = "Debit money from an account in a single currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully debited money",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found - The account or balance does not exist",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid CreditBalanceDTO payload",
                    content = @Content()),
            @ApiResponse(responseCode = "422", description = "Unprocessable entity - Insufficient balance",
                    content = @Content())
    })
    @PostMapping("/debit")
    public ResponseEntity<?> debitMoney(
            @Valid @RequestBody DebitBalanceDTO debitBalanceDTO,
            Errors errors) throws Exception {
        log.info("Debiting money from account...");
        // Validate request DTO
        if (errors.hasErrors()) {
            log.warn("Invalid request: {}", errors.getAllErrors());
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        // Fetch account by account number
        Account account = accountService.getAccountByAccountNumber(debitBalanceDTO.getAccountNumber());
        if (account == null) {
            log.warn("Account not found for account number: {}", debitBalanceDTO.getAccountNumber());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        // Debit money from account for the specified currency
        accountBalanceService.debitMoney(account, debitBalanceDTO, account.getAccountHolder());

        // Also save this step as a transaction
        TransactionCommand cmd = TransactionCommand.builder()
                .account(account)
                .amount(debitBalanceDTO.getAmount())
                .currency(debitBalanceDTO.getCurrency())
                .type(TransactionType.DEBIT)
                .createdBy(account.getAccountHolder())
                .build();
        transactionService.createNewTransaction(cmd);

        log.info("Debit successful");
        return ResponseEntity.ok("Debit successful");
    }


}
