package com.example.bank_account_app.dto;

import com.example.bank_account_app.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeCurrencyDTO {
    @Schema(description = "Account number", example = "EE123456789012345678")
    @NotBlank(message = "Account number cannot be empty")
    @Pattern(regexp = "^EE\\d{18}$", message = "Invalid account number format")
    private String accountNumber;

    @Schema(description = "Currency to exchange from", example = "EUR")
    @NotNull(message = "Currency is required")
    private Currency fromCurrency;

    @Schema(description = "Currency to exchange to", example = "USD")
    @NotNull(message = "Currency is required")
    private Currency toCurrency;

    @Schema(description = "Amount to exchange", example = "100.0")
    @Min(value = 0, message = "Amount must be greater than 0")
    private BigDecimal amount;
}
