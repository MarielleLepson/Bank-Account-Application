package com.example.bank_account_app.integration.controller;

import com.example.bank_account_app.dto.CreateAccountDTO;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.repository.AccountBalanceRepository;
import com.example.bank_account_app.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @BeforeEach
    void setUp() {
        accountBalanceRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void testGetAllAccounts_Empty() throws Exception {
        mockMvc.perform(get("/api/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setAccountHolder("John Doe");
        String json = objectMapper.writeValueAsString(createAccountDTO);

        mockMvc.perform(post("/api/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").exists());
    }

    @Test
    void testGetAccountByNumber_Invalid() throws Exception {
        mockMvc.perform(get("/api/account/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid account number"));
    }

    @Test
    void testGetAccountByNumber_NotFound() throws Exception {
        String accountNumber = "EE000000000000000000";
        mockMvc.perform(get("/api/account/" + accountNumber))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));
    }

    @Test
    void testGetAccountByNumber_Success() throws Exception {
        Account account = new Account();
        account.setAccountNumber("EE123456789012345678");
        account.setAccountHolder("Mart Tamm");
        account.setCreatedBy("integration test");
        accountRepository.save(account);

        mockMvc.perform(get("/api/account/" + account.getAccountNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()));
    }
}
