package com.example.bank_account_app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountDTO {
    @Schema(description = "Account holder name", example = "Karl Kask")
    private String accountHolder;
}
