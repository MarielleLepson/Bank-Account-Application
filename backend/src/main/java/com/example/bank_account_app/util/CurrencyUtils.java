package com.example.bank_account_app.util;

import com.example.bank_account_app.enums.Currency;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class CurrencyUtils {
    private static final Random RANDOM = new Random();

    /**
     * Fetches a random currency from the available currencies.
     */
    public static Currency getRandomCurrency() {
        List<Currency> currencies = List.of(Currency.values());
        return currencies.get(RANDOM.nextInt(currencies.size()));
    }
}
