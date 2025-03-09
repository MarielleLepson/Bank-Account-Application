package com.example.bank_account_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {
    @Schema(description = "Bank account number", example = "EE123456789012345678")
    private String accountNumber;
    @Schema(description = "Account balances for all supported currencies", example = "[{\"currency\":\"EUR\",\"balance\":\"100.00\"},{\"currency\":\"USD\",\"balance\":\"200.00\"}]")
    private List<CurrencyBalance> currencyBalances;
}
