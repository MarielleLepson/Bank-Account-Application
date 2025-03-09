package com.example.bank_account_app.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random account number in the format "EE" followed by 18 digits.
     */
    public static String generateAccountNumber() {
        long randomNumber = (long) (RANDOM.nextDouble() * 1_000_000_000_000_000_000L);
        return "EE" + String.format("%018d", randomNumber);
    }

    /**
     * Validates the given account number.
     * The account number must be a string of 20 characters starting with "EE".
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("^EE\\d{18}$");
    }

    /**
     * Validates the given account holder name. The name must contain only letters and spaces.
     */
    public static boolean isValidAccountHolder(String accountHolder) {
        return accountHolder != null && accountHolder.matches("^[a-zA-Z ]+$");
    }
}
