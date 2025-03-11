package com.example.bank_account_app.integration.controller;

import com.example.bank_account_app.dto.CreditBalanceDTO;
import com.example.bank_account_app.dto.ExchangeCurrencyDTO;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CurrencyExchangeControllerIT {

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
    void testExchangeCurrency_Floating_Success() throws Exception {
        ExchangeCurrencyDTO dto = new ExchangeCurrencyDTO();
        dto.setAccountNumber(account.getAccountNumber());
        dto.setFromCurrency(Currency.USD);
        dto.setToCurrency(Currency.EUR);
        dto.setAmount(new BigDecimal("100.00"));
        String json = objectMapper.writeValueAsString(dto);

        CreditBalanceDTO creditDTO = new CreditBalanceDTO();
        creditDTO.setAccountNumber(account.getAccountNumber());
        creditDTO.setAmount(100.0);
        creditDTO.setCurrency(Currency.USD);
        String creditJson = objectMapper.writeValueAsString(creditDTO);

        mockMvc.perform(post("/api/account-balance/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(creditJson));


        mockMvc.perform(post("/api/currency-exchange/floating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()));
    }

    @Test
    void testExchangeCurrency_Fixed_Success() throws Exception {
        // if the rate is  Currency.USD, to Currency.EUR, 0.91,
        ExchangeCurrencyDTO dto = new ExchangeCurrencyDTO();
        dto.setAccountNumber(account.getAccountNumber());
        dto.setFromCurrency(Currency.USD);
        dto.setToCurrency(Currency.EUR);
        dto.setAmount(new BigDecimal("100.00"));
        String json = objectMapper.writeValueAsString(dto);

        CreditBalanceDTO creditDTO = new CreditBalanceDTO();
        creditDTO.setAccountNumber(account.getAccountNumber());
        creditDTO.setAmount(100.0);
        creditDTO.setCurrency(Currency.USD);
        String creditJson = objectMapper.writeValueAsString(creditDTO);

        mockMvc.perform(post("/api/account-balance/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(creditJson));

        mockMvc.perform(post("/api/currency-exchange/fixed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()))
                .andExpect(jsonPath("$.currencyBalances[0].currency").value(Currency.USD.name()))
                .andExpect(jsonPath("$.currencyBalances[0].balance").value("9.00"))
                .andExpect(jsonPath("$.currencyBalances[1].currency").value(Currency.EUR.name()))
                .andExpect(jsonPath("$.currencyBalances[1].balance").value("91.00"));



    }
}

