package com.example.bank_account_app.repository;

import com.example.bank_account_app.model.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for AccountBalance entity to perform CRUD operations.
 */
@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {

    // find all account balances by account id
    List<AccountBalance> findAllByAccountId(Long accountId);


}
