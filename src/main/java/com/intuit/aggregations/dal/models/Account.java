package com.intuit.aggregations.dal.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intuit.aggregations.controllers.domain.types.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="accounts")
public class Account {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "accountId", nullable = false)
    private String accountId;

    @Column(name = "accountName")
    private String accountName;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "currency")
    @Enumerated(EnumType.ORDINAL)
    private Currency currency;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionModel> transactions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source", nullable = false)
    @JsonIgnore
    private Source source;


    public Account(String accountId, String accountName, BigDecimal balance, Currency currency, Source source) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.balance = balance;
        this.currency = currency;
        this.source = source;
        this.transactions = new ArrayList<>();
    }

}

