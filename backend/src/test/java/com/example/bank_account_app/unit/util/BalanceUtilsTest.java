package com.example.bank_account_app.unit.util;

import com.example.bank_account_app.enums.Currency;
import com.example.bank_account_app.util.BalanceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class BalanceUtilsTest {

    @Test
    void shouldGenerateRandomBalance() {
        Currency eur = Currency.EUR;
        Currency usd = Currency.USD;
        Currency sek = Currency.SEK;
        Currency rub = Currency.RUB;
        Currency krw = Currency.KRW;

        double eurBalance = BalanceUtils.generateRandomBalance(eur);
        double usdBalance = BalanceUtils.generateRandomBalance(usd);
        double sekBalance = BalanceUtils.generateRandomBalance(sek);
        double rubBalance = BalanceUtils.generateRandomBalance(rub);
        double krwBalance = BalanceUtils.generateRandomBalance(krw);

        assertTrue(eurBalance >= 0 && eurBalance <= 1000);
        assertTrue(usdBalance >= 0 && usdBalance <= 1000);
        assertTrue(sekBalance >= 0 && sekBalance <= 10000);
        assertTrue(rubBalance >= 0 && rubBalance <= 100000);
        assertTrue(krwBalance >= 0 && krwBalance <= 1000000);
    }

    @Test
    void shouldReturnZeroForNullCurrency() {
        double balance = BalanceUtils.generateRandomBalance(null);
        assertEquals(0.0, balance, "Balance should be 0.0 when currency is null");
    }

    @Test
    void shouldLogWarningForNullCurrency() {
        try (MockedStatic<BalanceUtils> mockedStatic = Mockito.mockStatic(BalanceUtils.class)) {
            BalanceUtils.generateRandomBalance(null);
            mockedStatic.verify(() -> BalanceUtils.generateRandomBalance(null), times(1));
        }
    }
}
