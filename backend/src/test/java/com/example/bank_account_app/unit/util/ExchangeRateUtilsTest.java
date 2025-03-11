package com.example.bank_account_app.unit.util;


import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.util.ExchangeRateUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExchangeRateUtilsTest {

    @Test
    void testGetExchangeRateForSameCurrency() {
        Currency from = Currency.EUR;
        Currency to = Currency.EUR;
        double expected = 1.0;
        double actual = ExchangeRateUtils.getExchangeRate(from, to);

        assertEquals(expected, actual);
    }

    @Test
    void testGetExchangeRateForDifferentCurrency() {
        Currency from = Currency.EUR;
        Currency to = Currency.USD;
        double expected = 1.10;
        double actual = ExchangeRateUtils.getExchangeRate(from, to);

        assertEquals(expected, actual);
    }

    @Test
    void testGetExchangeRateForDifferentCurrencyV2() {
        Currency from = Currency.USD;
        Currency to = Currency.EUR;
        double expected = 0.91;
        double actual = ExchangeRateUtils.getExchangeRate(from, to);

        assertEquals(expected, actual);
    }

}
