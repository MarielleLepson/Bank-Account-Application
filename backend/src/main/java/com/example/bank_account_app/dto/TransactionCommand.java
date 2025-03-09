package com.example.bank_account_app.dto;

import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.enums.TransactionType;
import com.example.bank_account_app.model.Account;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class TransactionCommand {
    private Currency currency;
    private double amount;
    private Account account;
    private TransactionType type;
    private String createdBy;
}
