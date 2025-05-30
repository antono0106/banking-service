package com.moroz.bankingservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
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
    //for some reason, hibernate doesn't take into account default value of BigDecimal,
    //so it must be set directly into the code
    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    public Account(
            final Long id, final String firstName, final String lastName, final String email, final BigDecimal balance
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.balance = balance;
    }

    public Account(
            final Long id, final String firstName, final String lastName, final String email, final long balance
    ) {
        this(id, firstName, lastName, email, BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_UP));
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = balance.setScale(2, RoundingMode.HALF_UP);
    }

    public void setBalance(final long balance) {
        setBalance(BigDecimal.valueOf(balance));
    }

}
