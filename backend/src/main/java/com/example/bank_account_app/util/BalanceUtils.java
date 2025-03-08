package com.example.bank_account_app.util;

import com.example.bank_account_app.enums.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BalanceUtils {
    /**
     * Generates a random balance that currency (e.g. 0-1000 for EUR).
     */
    public static double generateRandomBalance(Currency currency) {
        if (currency == null) {
            log.warn("Empty currency provided");
            return 0;
        }
        return switch (currency) {
            case EUR, USD -> Math.random() * 1000;
            case SEK -> Math.random() * 10000;
            case RUB -> Math.random() * 100000;
            case KRW -> Math.random() * 1000000;
            default -> throw new IllegalArgumentException("Unsupported currency provided");

        };
    }
}
