package com.example.bank_account_app.model;

import com.example.bank_account_app.enums.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Since one account can hold multiple balances in different currencies,
 * this entity represents a balance of an account in a specific currency.
 */
@Builder
@Entity
@Table(name = "account_balances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountBalance {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private int id; // unique account balance ID

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "account_id", nullable = false)
        private Account account; // account to which this balance belongs

        @Enumerated(EnumType.STRING)
        @Column(name = "currency", nullable = false, length = 3)
        private Currency currency; // currency of the balance

        @Column(name = "balance", nullable = false, precision = 15, scale = 2)
        private BigDecimal balance; // current balance of the account

        @CreationTimestamp
        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt; // timestamp of when the balance was created

        @Column(name = "created_by", nullable = false)
        private String createdBy; // the user who created the balance

        @Column(name = "last_modified_at")
        private LocalDateTime lastModifiedAt; // timestamp of when the balance was last modified

        @Column(name = "last_modified_by")
        private String lastModifiedBy; // the user who last modified the balance
}
