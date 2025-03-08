package com.example.bank_account_app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @GetMapping("/accounts")
    public String getAccounts() {
        return "List of accounts";
    }
}

