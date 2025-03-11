package com.example.bank_account_app.service;

import com.example.bank_account_app.dto.TransactionCommand;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.enums.TransactionType;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.Transaction;
import com.example.bank_account_app.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveTransaction() {
        Transaction transaction = new Transaction();
        transactionService.saveTransaction(transaction);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void shouldCreateNewDebitTransaction() {
        TransactionCommand cmd = TransactionCommand.builder()
                .account(new Account())
                .amount(100)
                .currency(Currency.EUR)
                .type(TransactionType.DEBIT)
                .createdBy("user")
                .build();

        transactionService.createNewTransaction(cmd);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldCreateNewCreditTransaction() {
        TransactionCommand cmd = TransactionCommand.builder()
                .account(new Account())
                .amount(100)
                .currency(Currency.EUR)
                .type(TransactionType.CREDIT)
                .createdBy("user")
                .build();

        transactionService.createNewTransaction(cmd);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
