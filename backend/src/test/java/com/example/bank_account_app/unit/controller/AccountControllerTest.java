package com.example.bank_account_app.unit.controller;

import com.example.bank_account_app.controller.AccountController;
import com.example.bank_account_app.dto.AccountDTO;
import com.example.bank_account_app.dto.CreateAccountDTO;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.service.AccountService;
import com.example.bank_account_app.util.AccountUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllAccounts() throws Exception {
        Account account = new Account();
        account.setAccountNumber("EE123456789012345678");
        List<Account> accounts = Collections.singletonList(account);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber("EE123456789012345678");
        List<AccountDTO> accountDTOs = Collections.singletonList(accountDTO);

        when(accountService.getAllAccounts()).thenReturn(accounts);
        when(accountService.mapAccountsToDTO(accounts)).thenReturn(accountDTOs);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountNumber").value("EE123456789012345678"));
    }

    @Test
    void testGetAccountByNumber_InvalidAccountNumber() throws Exception {
        String invalidAccountNumber = "invalid";
        mockMvc.perform(get("/api/account/" + invalidAccountNumber))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid account number"));
    }

    @Test
    void testGetAccountByNumber_NotFound() throws Exception {
        String accountNumber = "EE123456789012345678";
        when(accountService.getAccountByAccountNumber(accountNumber)).thenReturn(null);

        mockMvc.perform(get("/api/account/" + accountNumber))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));
    }

    @Test
    void testGetAccountByNumber_Success() throws Exception {
        String accountNumber = "EE123456789012345678";
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        when(accountService.getAccountByAccountNumber(accountNumber)).thenReturn(account);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(accountNumber);
        when(accountService.mapAccountToDTO(account)).thenReturn(accountDTO);

        mockMvc.perform(get("/api/account/" + accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber));
    }

    @Test
    void testCreateAccount_InvalidAccountHolder() throws Exception {
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setAccountHolder("Invalid123");
        String json = objectMapper.writeValueAsString(createAccountDTO);

        mockMvc.perform(post("/api/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid account holder name"));
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setAccountHolder("Mari Maasikas");
        String json = objectMapper.writeValueAsString(createAccountDTO);

        try (MockedStatic<AccountUtils> accountUtilsMock = mockStatic(AccountUtils.class)) {
            accountUtilsMock.when(() -> AccountUtils.isValidAccountHolder("Mari Maasikas")).thenReturn(true);
            accountUtilsMock.when(AccountUtils::generateAccountNumber)
                    .thenReturn("EE123456789012345678");

            Account account = new Account();
            account.setAccountNumber("EE123456789012345678");
            when(accountService.buildAccountEntity("EE123456789012345678", "Mari Maasikas", "Mari Maasikas"))
                    .thenReturn(account);
            doNothing().when(accountService).saveAccount(account);

            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setAccountNumber("EE123456789012345678");
            when(accountService.mapAccountToDTO(account)).thenReturn(accountDTO);

            mockMvc.perform(post("/api/account/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountNumber").value("EE123456789012345678"));
        }
    }
}

