package com.example.bank_account_app.unit.controller;

import com.example.bank_account_app.controller.AccountBalanceController;
import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.CreditBalanceDTO;
import com.example.bank_account_app.dto.DebitBalanceDTO;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.model.AccountBalance;
import com.example.bank_account_app.service.AccountBalanceService;
import com.example.bank_account_app.service.AccountService;
import com.example.bank_account_app.service.TransactionService;
import com.example.bank_account_app.util.AccountUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AccountBalanceControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AccountBalanceService accountBalanceService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    private AccountBalanceController accountBalanceController;

    @BeforeEach
    void setUp() {
        accountBalanceController = new AccountBalanceController(accountBalanceService, accountService, transactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(accountBalanceController).build();
    }

    @Test
    void testGetAccountBalance_InvalidAccountNumber() throws Exception {
        String invalidAccountNumber = "invalid";
        try (MockedStatic<AccountUtils> accountUtilsMock = mockStatic(AccountUtils.class)) {
            accountUtilsMock.when(() -> AccountUtils.isValidAccountNumber(invalidAccountNumber)).thenReturn(false);

            mockMvc.perform(get("/api/account-balance/" + invalidAccountNumber))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid account number"));
        }
    }

    @Test
    void testGetAccountBalance_AccountNotFound() throws Exception {
        String validAccountNumber = "EE123456789012345678";
        try (MockedStatic<AccountUtils> accountUtilsMock = mockStatic(AccountUtils.class)) {
            accountUtilsMock.when(() -> AccountUtils.isValidAccountNumber(validAccountNumber)).thenReturn(true);
            when(accountService.getAccountByAccountNumber(validAccountNumber)).thenReturn(null);

            mockMvc.perform(get("/api/account-balance/" + validAccountNumber))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Account not found"));
        }
    }

    @Test
    void testGetAccountBalance_Success() throws Exception {
        String validAccountNumber = "EE123456789012345678";
        Account account = new Account();
        account.setAccountNumber(validAccountNumber);

        AccountBalance balance = new AccountBalance();
        balance.setBalance(new BigDecimal("100"));
        List<AccountBalance> balances = Collections.singletonList(balance);

        AccountBalanceDTO balanceDTO = new AccountBalanceDTO();
        balanceDTO.setAccountNumber(validAccountNumber);

        try (MockedStatic<AccountUtils> accountUtilsMock = mockStatic(AccountUtils.class)) {
            accountUtilsMock.when(() -> AccountUtils.isValidAccountNumber(validAccountNumber)).thenReturn(true);
            when(accountService.getAccountByAccountNumber(validAccountNumber)).thenReturn(account);
            when(accountBalanceService.getAccountBalances(account)).thenReturn(balances);
            when(accountBalanceService.mapAccountBalancesToDTO(balances, account)).thenReturn(balanceDTO);

            mockMvc.perform(get("/api/account-balance/" + validAccountNumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountNumber").value(validAccountNumber));
        }
    }


    @Test
    void testDepositMoney_InvalidPayload() throws Exception {
        String invalidJson = "{}";
        mockMvc.perform(post("/api/account-balance/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDepositMoney_AccountNotFound() throws Exception {
        CreditBalanceDTO creditDTO = new CreditBalanceDTO();
        creditDTO.setAccountNumber("EE123456789012345678");
        creditDTO.setAmount(100);
        creditDTO.setCurrency(Currency.USD);

        String json = objectMapper.writeValueAsString(creditDTO);
        when(accountService.getAccountByAccountNumber(creditDTO.getAccountNumber())).thenReturn(null);

        mockMvc.perform(post("/api/account-balance/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));
    }

    @Test
    void testDepositMoney_Success() throws Exception {
        CreditBalanceDTO creditDTO = new CreditBalanceDTO();
        creditDTO.setAccountNumber("EE123456789012345678");
        creditDTO.setAmount(100);
        creditDTO.setCurrency(Currency.USD);

        String json = objectMapper.writeValueAsString(creditDTO);
        Account account = new Account();
        account.setAccountNumber("EE123456789012345678");
        account.setAccountHolder("Mari Maasikas");

        when(accountService.getAccountByAccountNumber(creditDTO.getAccountNumber())).thenReturn(account);
        doNothing().when(accountBalanceService).creditMoney(account, creditDTO, account.getAccountHolder());
        doNothing().when(transactionService).createNewTransaction(any());

        mockMvc.perform(post("/api/account-balance/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit successful"));
    }

    @Test
    void testDebitMoney_InvalidPayload() throws Exception {
        String invalidJson = "{}";
        mockMvc.perform(post("/api/account-balance/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDebitMoney_AccountNotFound() throws Exception {
        DebitBalanceDTO debitDTO = new DebitBalanceDTO();
        debitDTO.setAccountNumber("EE123456789012345678");
        debitDTO.setAmount(50.0);
        debitDTO.setCurrency(Currency.USD);

        String json = objectMapper.writeValueAsString(debitDTO);
        when(accountService.getAccountByAccountNumber(debitDTO.getAccountNumber())).thenReturn(null);

        mockMvc.perform(post("/api/account-balance/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));
    }

    @Test
    void testDebitMoney_Success() throws Exception {
        DebitBalanceDTO debitDTO = new DebitBalanceDTO();
        debitDTO.setAccountNumber("EE123456789012345678");
        debitDTO.setAmount(50.0);
        debitDTO.setCurrency(Currency.USD);

        String json = objectMapper.writeValueAsString(debitDTO);
        Account account = new Account();
        account.setAccountNumber("EE123456789012345678");
        account.setAccountHolder("Mari Maasikas");

        when(accountService.getAccountByAccountNumber(debitDTO.getAccountNumber())).thenReturn(account);
        doNothing().when(accountBalanceService).debitMoney(account, debitDTO, account.getAccountHolder());
        doNothing().when(transactionService).createNewTransaction(any());

        mockMvc.perform(post("/api/account-balance/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Debit successful"));
    }
}

