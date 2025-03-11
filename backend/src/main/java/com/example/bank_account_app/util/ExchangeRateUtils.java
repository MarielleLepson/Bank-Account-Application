package com.example.bank_account_app.util;

import com.example.bank_account_app.enums.Currency;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Utility class to get the exchange rate between two currencies.
 */
@Component
public class ExchangeRateUtils {
    private static final Map<Currency, Map<Currency, Double>> FIXED_RATES = Map.of(
            Currency.EUR, Map.of(
                    Currency.USD, 1.10,
                    Currency.SEK, 11.50,
                    Currency.RUB, 95.00,
                    Currency.KRW, 1450.00
            ),
            Currency.USD, Map.of(
                    Currency.EUR, 0.91,
                    Currency.SEK, 10.45,
                    Currency.RUB, 86.36,
                    Currency.KRW, 1318.18
            ),
            Currency.SEK, Map.of(
                    Currency.EUR, 0.087,
                    Currency.USD, 0.096,
                    Currency.RUB, 8.27,
                    Currency.KRW, 126.19
            ),
            Currency.RUB, Map.of(
                    Currency.EUR, 0.0105,
                    Currency.USD, 0.0116,
                    Currency.SEK, 0.121,
                    Currency.KRW, 15.25
            ),
            Currency.KRW, Map.of(
                    Currency.EUR, 0.00069,
                    Currency.USD, 0.00076,
                    Currency.SEK, 0.0079,
                    Currency.RUB, 0.066
            )
    );

    /**
     * Get the exchange rate between two currencies.
     */
    public static double getExchangeRate(Currency from, Currency to) {
        if (from == to) {
            return 1.0;
        }

        if (!FIXED_RATES.containsKey(from) || !FIXED_RATES.get(from).containsKey(to)) {
            throw new IllegalArgumentException("Exchange rate not found for " + from + " -> " + to);
        }
        return FIXED_RATES.get(from).get(to);
    }
}

