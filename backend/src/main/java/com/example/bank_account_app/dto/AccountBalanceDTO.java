package com.example.bank_account_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {
    private String currency;
    private String balance;
}
