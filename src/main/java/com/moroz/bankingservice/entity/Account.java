package com.moroz.bankingservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "accounts")
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "first_name")
    private String firstName;
    @Column(nullable = false, name = "last_name")
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    private long balance;
    private int cents;

    public void setBalance(final long balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    public void setCents(final int cents) {
        if (cents < 0 || cents > 99) {
            throw new IllegalArgumentException("Cents range must be from 0 to 99");
        }
        this.cents = cents;
    }
}
