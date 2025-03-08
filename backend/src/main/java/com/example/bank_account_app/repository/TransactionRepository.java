package com.example.bank_account_app.repository;

import com.example.bank_account_app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This is a repository interface for Transaction entity to perform CRUD operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // TODO: ML-e: Add custom queries here
}
