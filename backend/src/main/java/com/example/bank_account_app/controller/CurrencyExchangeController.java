package com.example.bank_account_app.controller;

import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.ExchangeCurrencyDTO;
import com.example.bank_account_app.service.AccountService;
import com.example.bank_account_app.service.CurrencyExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/currency-exchange")
@RequiredArgsConstructor
public class CurrencyExchangeController {
    private final CurrencyExchangeService currencyExchangeService;
    private final AccountService accountService;

    /**
     * Gives the exchange rate between two currencies from external API and does the exchange.
     */
    @Operation(summary = "Currency exchange using External API", description = "Returns the exchange rate between two currencies and does the exchange")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully exchanged currency",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found - The account does not exist",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid account number",
                    content = @Content())
    })
    @PostMapping("/floating")
    public ResponseEntity<?> exchangeCurrency(
            @Valid @RequestBody ExchangeCurrencyDTO exchangeCurrencyDTO,
            Errors errors) {
        log.info("Exchange currency request: {}", exchangeCurrencyDTO);

        // Validate the request
        if (errors.hasErrors()) {
            log.warn("Invalid exchange currency request: {}", errors.getAllErrors());
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        //  Check if account exists
        var account = accountService.getAccountByAccountNumber(exchangeCurrencyDTO.getAccountNumber());
        if (account == null) {
            log.warn("Account not found: {}", exchangeCurrencyDTO.getAccountNumber());
            return ResponseEntity.notFound().build();
        }

        // Fetch the exchange rate from an external API and do the exchange
        AccountBalanceDTO results = currencyExchangeService.exchangeCurrency(exchangeCurrencyDTO, account, account.getAccountHolder());
        return ResponseEntity.ok(results);
    }

    /**
     * Gives the exchange rate between two currencies from fixed rate and does the exchange.
     */
    @Operation(summary = "Feature 4: Currency exchange with fixed rate", description = "Returns the exchange rate between two currencies and does the exchange")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully exchanged currency",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Not found - The account does not exist",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid account number",
                    content = @Content())
    })
    @PostMapping("/fixed")
    public ResponseEntity<?> exchangeCurrencyFixed(
            @Valid @RequestBody ExchangeCurrencyDTO exchangeCurrencyDTO,
            Errors errors) {
        log.info("Exchange currency request: {}", exchangeCurrencyDTO);

        // Validate the request
        if (errors.hasErrors()) {
            log.warn("Invalid exchange currency request: {}", errors.getAllErrors());
            return ResponseEntity.badRequest().body(errors.getAllErrors());
        }

        //  Check if account exists
        var account = accountService.getAccountByAccountNumber(exchangeCurrencyDTO.getAccountNumber());
        if (account == null) {
            log.warn("Account not found: {}", exchangeCurrencyDTO.getAccountNumber());
            return ResponseEntity.notFound().build();
        }

        // Fetch the exchange rate from fixed rate and do the exchange
        AccountBalanceDTO results = currencyExchangeService.exchangeCurrencyFixed(exchangeCurrencyDTO, account, account.getAccountHolder());
        return ResponseEntity.ok(results);
    }
}
