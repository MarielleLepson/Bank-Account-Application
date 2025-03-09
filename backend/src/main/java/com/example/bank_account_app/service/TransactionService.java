package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.TransactionCommand;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.enums.TransactionType;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.model.Transaction;
import com.example.bank_account_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;


    /**
     * Fetches all transactions from the database.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Save transaction to the database.
     */
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        log.debug("New transaction created successfully");
    }

    /**
     * Create a new transaction
     */
    public void createNewTransaction(TransactionCommand cmd) {
        log.debug("Creating new transaction...");
        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(cmd.getAccount());
        newTransaction.setTransactionType(cmd.getType());
        newTransaction.setAmount(BigDecimal.valueOf(cmd.getAmount()));
        newTransaction.setCurrency(cmd.getCurrency());
        newTransaction.setTransactionDate(LocalDateTime.now());
        newTransaction.setCreatedBy(cmd.getCreatedBy());
        saveTransaction(newTransaction);
    }
}
