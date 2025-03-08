package com.example.bank_account_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a bank account.
 */
@Builder
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id; // unique account ID

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber; // unique account number

    @Column(name = "account_holder", nullable = false)
    private String accountHolder; // account holder's name

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // timestamp of when the account was created

    @Column(name = "created_by", nullable = false)
    private String createdBy; // the user who created the bank account
}
