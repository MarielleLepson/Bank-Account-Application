package com.example.bank_account_app.integration.controller;

import com.example.bank_account_app.dto.CreditBalanceDTO;
import com.example.bank_account_app.dto.DebitBalanceDTO;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import com.example.bank_account_app.repository.AccountRepository;
import com.example.bank_account_app.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountBalanceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountBalanceRepository.deleteAll();
        accountRepository.deleteAll();
        account = new Account();
        account.setAccountNumber("EE123456789012345678");
        account.setAccountHolder("Mari Maasikas");
        account.setCreatedBy("integration test");
        account = accountRepository.save(account);
    }

    @Test
    void testGetAccountBalance_InvalidAccountNumber() throws Exception {
        mockMvc.perform(get("/api/account-balances/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid account number"));
    }

    @Test
    void testGetAccountBalance_AccountNotFound() throws Exception {
        String nonExisting = "EE000000000000000000";
        mockMvc.perform(get("/api/account-balances/" + nonExisting))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));
    }

    @Test
    void testGetAccountBalance_Success() throws Exception {
        mockMvc.perform(get("/api/account-balances/" + account.getAccountNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()));
    }

    @Test
    void testDepositMoney_Success() throws Exception {
        CreditBalanceDTO creditDTO = new CreditBalanceDTO();
        creditDTO.setAccountNumber(account.getAccountNumber());
        creditDTO.setAmount(100.0);
        creditDTO.setCurrency(Currency.USD);
        String json = objectMapper.writeValueAsString(creditDTO);

        mockMvc.perform(post("/api/account-balance/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit/Credit successful"));
    }

    @Test
    void testDebitMoney_Success() throws Exception {
        CreditBalanceDTO creditDTO = new CreditBalanceDTO();
        creditDTO.setAccountNumber(account.getAccountNumber());
        creditDTO.setAmount(100.0);
        creditDTO.setCurrency(Currency.USD);
        String creditJson = objectMapper.writeValueAsString(creditDTO);

        mockMvc.perform(post("/api/account-balance/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creditJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit/Credit successful"));

        DebitBalanceDTO debitDTO = new DebitBalanceDTO();
        debitDTO.setAccountNumber(account.getAccountNumber());
        debitDTO.setAmount(50.0);
        debitDTO.setCurrency(Currency.USD);
        String debitJson = objectMapper.writeValueAsString(debitDTO);

        mockMvc.perform(post("/api/account-balance/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(debitJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Debit successful"));
    }
}

