package com.example.bank_account_app.repository;

import com.example.bank_account_app.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This is a repository interface for Account entity to perform CRUD operations.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // TODO: ML-e: Add custom queries here
}
