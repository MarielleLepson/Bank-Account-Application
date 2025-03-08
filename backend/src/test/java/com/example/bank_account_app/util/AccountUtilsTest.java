package com.example.bank_account_app.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountUtilsTest {

    @Test
    void shouldGenerateValidAccountNumber() {
        String accountNumber = AccountUtils.generateAccountNumber();

        assertNotNull(accountNumber, "Generated account number should not be null");
        assertEquals(20, accountNumber.length(), "Generated account number should be 20 characters long");
        assertTrue(accountNumber.startsWith("EE"), "Generated account number should start with 'EE'");
        assertTrue(accountNumber.matches("^EE\\d{18}$"), "Generated account number should follow the correct format");
    }

    @Test
    void shouldValidateCorrectAccountNumber() {
        String validAccountNumber = "EE123456789012345678";
        assertTrue(AccountUtils.isValidAccountNumber(validAccountNumber), "Valid account number should be recognized");
    }

    @Test
    void shouldInvalidateIncorrectAccountNumber() {
        String tooShortAccountNumber = "EE123";
        String missingPrefix = "12345678901234567890";
        String nullAccountNumber = null;

        assertFalse(AccountUtils.isValidAccountNumber(tooShortAccountNumber), "Too short account number should be invalid");
        assertFalse(AccountUtils.isValidAccountNumber(missingPrefix), "Account number missing 'EE' should be invalid");
        assertFalse(AccountUtils.isValidAccountNumber(nullAccountNumber), "Null account number should be invalid");
    }
}

