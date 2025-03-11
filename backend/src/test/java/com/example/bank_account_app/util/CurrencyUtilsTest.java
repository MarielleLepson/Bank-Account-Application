package com.example.bank_account_app.util;

import com.example.bank_account_app.enums.Currency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyUtilsTest {

    @Test
    void testGetRandomCurrency() {
        Currency currency = CurrencyUtils.getRandomCurrency();
        List<Currency> availableCurrencies = List.of(Currency.values());

        assertNotNull(currency);
        assertTrue(availableCurrencies.contains(currency));
    }

    @Test
    void testGetRandomCurrencyMultipleTimes() {
        for (int i = 0; i < 10; i++) {
            testGetRandomCurrency();
        }
    }

    @Test
    void testGetSupportedCurrencies() {
        List<Currency> supportedCurrencies = CurrencyUtils.getSupportedCurrencies();
        List<Currency> availableCurrencies = List.of(Currency.values());

        assertNotNull(supportedCurrencies);
        assertTrue(supportedCurrencies.containsAll(availableCurrencies));
    }

}
