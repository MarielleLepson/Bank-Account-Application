package com.example.bank_account_app.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Configuration
@Validated
public class CurrencyExchangeConf {

    @NotNull
    @Value("${bank-account-app.timeout:2s}")
    private Duration timeout;

    @NotBlank
    @Value("${bank-account-app.currency-exchange.endpoint}")
    private String currencyExchangeEndpoint;
}