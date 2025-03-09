package com.example.bank_account_app.dto;


import com.example.bank_account_app.model.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    @Schema(description = "Bank account number", example = "EE123456789012345678")
    private String accountNumber;

    @Schema(description = "Name of the account holder", example = "Karl Kask")
    private String accountHolder;

    /**
     * Converts an Account entity to a DTO.
     */
    public AccountDTO toDTO(Account account) {
        return new AccountDTO(
                account.getAccountNumber(),
                account.getAccountHolder()
        );
    }
}
