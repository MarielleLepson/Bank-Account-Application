package com.example.bank_account_app.util;

import com.example.bank_account_app.enums.Currency;
import org.springframework.stereotype.Component;

@Component
public class BalanceUtils {
    /**
     * Generates a random balance that currency (e.g. 0-1000 for EUR).
     */
    public static double generateRandomBalance(Currency currency) {
        return switch (currency) {
            case EUR, USD -> Math.random() * 1000;
            case SEK -> Math.random() * 10000;
            case RUB -> Math.random() * 100000;
            case KRW -> Math.random() * 1000000;
        };
    }
}
