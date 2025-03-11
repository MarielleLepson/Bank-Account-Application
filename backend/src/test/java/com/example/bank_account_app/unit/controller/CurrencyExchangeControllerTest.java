package com.example.bank_account_app.unit.controller;

import com.example.bank_account_app.controller.CurrencyExchangeController;
import com.example.bank_account_app.dto.AccountBalanceDTO;
import com.example.bank_account_app.dto.ExchangeCurrencyDTO;
import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.model.Account;
import com.example.bank_account_app.service.AccountService;
import com.example.bank_account_app.service.CurrencyExchangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CurrencyExchangeService currencyExchangeService;

    @Mock
    private AccountService accountService;
    
    private CurrencyExchangeController currencyExchangeController;

    @BeforeEach
    void setUp() {
        currencyExchangeController = new CurrencyExchangeController(currencyExchangeService, accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(currencyExchangeController).build();
    }

    @Test
    void testExchangeCurrency_Floating_InvalidPayload() throws Exception {
        String invalidJson = "{}";
        mockMvc.perform(post("/api/currency-exchange/floating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testExchangeCurrency_Floating_AccountNotFound() throws Exception {
        ExchangeCurrencyDTO exchangeDTO = new ExchangeCurrencyDTO();
        exchangeDTO.setAccountNumber("EE123456789012345678");
        exchangeDTO.setFromCurrency(Currency.USD);
        exchangeDTO.setToCurrency(Currency.EUR);
        String json = objectMapper.writeValueAsString(exchangeDTO);
        
        when(accountService.getAccountByAccountNumber("EE123456789012345678")).thenReturn(null);

        mockMvc.perform(post("/api/currency-exchange/floating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testExchangeCurrency_Floating_Success() throws Exception {
        ExchangeCurrencyDTO exchangeDTO = new ExchangeCurrencyDTO();
        exchangeDTO.setAccountNumber("EE123456789012345678");
        exchangeDTO.setFromCurrency(Currency.USD);
        exchangeDTO.setToCurrency(Currency.EUR);
        String json = objectMapper.writeValueAsString(exchangeDTO);
        
        Account account = new Account();
        account.setAccountNumber("EE123456789012345678");
        account.setAccountHolder("Mari Maasikas");

        when(accountService.getAccountByAccountNumber("EE123456789012345678")).thenReturn(account);

        AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();
        accountBalanceDTO.setAccountNumber("EE123456789012345678");

        when(currencyExchangeService.exchangeCurrency(exchangeDTO, account, "Mari Maasikas"))
                .thenReturn(accountBalanceDTO);

        mockMvc.perform(post("/api/currency-exchange/floating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("EE123456789012345678"));
    }

    @Test
    void testExchangeCurrencyFixed_InvalidPayload() throws Exception {
        String invalidJson = "{}";
        mockMvc.perform(post("/api/currency-exchange/fixed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testExchangeCurrencyFixed_AccountNotFound() throws Exception {
        ExchangeCurrencyDTO exchangeDTO = new ExchangeCurrencyDTO();
        exchangeDTO.setAccountNumber("EE123456789012345678");
        exchangeDTO.setFromCurrency(Currency.USD);
        exchangeDTO.setToCurrency(Currency.EUR);
        String json = objectMapper.writeValueAsString(exchangeDTO);

        when(accountService.getAccountByAccountNumber("EE123456789012345678")).thenReturn(null);

        mockMvc.perform(post("/api/currency-exchange/fixed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testExchangeCurrencyFixed_Success() throws Exception {
        ExchangeCurrencyDTO exchangeDTO = new ExchangeCurrencyDTO();
        exchangeDTO.setAccountNumber("EE123456789012345678");
        exchangeDTO.setFromCurrency(Currency.EUR);
        exchangeDTO.setToCurrency(Currency.USD);
        String json = objectMapper.writeValueAsString(exchangeDTO);

        Account account = new Account();
        account.setAccountNumber("EE123456789012345678");
        account.setAccountHolder("Mari Maasikas");

        when(accountService.getAccountByAccountNumber("EE123456789012345678")).thenReturn(account);

        AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();
        accountBalanceDTO.setAccountNumber("EE123456789012345678");

        when(currencyExchangeService.exchangeCurrencyFixed(exchangeDTO, account, "Mari Maasikas"))
                .thenReturn(accountBalanceDTO);

        mockMvc.perform(post("/api/currency-exchange/fixed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("EE123456789012345678"));
    }
}

