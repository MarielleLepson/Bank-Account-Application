package com.example.bank_account_app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountBalance {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private int id;

        @ManyToOne
        @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
        private Account account;

        @Column(name = "currency", nullable = false, length = 3)
        private String currency;

        @Column(name = "balance", nullable = false, precision = 15, scale = 2)
        private BigDecimal balance;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "created_by", nullable = false)
        private String createdBy;

        @UpdateTimestamp
        @Column(name = "last_modified_at", nullable = false)
        private LocalDateTime lastModifiedAt;

        @Column(name = "last_modified_by", nullable = false)
        private String lastModifiedBy;
}
