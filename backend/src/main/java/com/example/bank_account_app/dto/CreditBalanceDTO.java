package com.example.bank_account_app.dto;

import com.example.bank_account_app.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditBalanceDTO {

    @Schema(description = "Bank account number", example = "EE123456789012345678")
    @NotBlank(message = "Account number cannot be empty")
    @Pattern(regexp = "^EE\\d{18}$", message = "Invalid account number format")
    private String accountNumber;

    @Schema(description = "Currency of the amount", example = "EUR")
    @NotNull(message = "Currency is required")
    private Currency currency;

    @Schema(description = "Amount to credit", example = "100.0")
    @Min(value = 0, message = "Amount must be greater than 0")
    private double amount;
}
