package com.example.bank_account_app.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountByAccountNumberDTO {

    @Schema(description = "Unique account ID", example = "1")
    private int id;

    @Schema(description = "Bank account number", example = "EE123456789012345678")
    private String accountNumber;

    @Schema(description = "Name of the account holder", example = "John Doe")
    private String accountHolder;

    @Schema(description = "The time when the account was created", example = "2021-12-31T23:59:59")
    private LocalDateTime createdAt;

    @Schema(description = "The user who created the bank account", example = "admin")
    private String createdBy;
}
