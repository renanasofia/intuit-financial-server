package com.intuit.aggregations.dal.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intuit.aggregations.controllers.domain.types.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="transactions")
public class TransactionModel {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "transactionId", nullable = false)
    private String transactionId;

    @Column(name = "date")
    private Long date;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Currency currency;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "account_id", nullable = false)
        @JsonIgnore
        private Account account;



    public TransactionModel(String transactionId, Long date, BigDecimal amount, Currency currency, String description, Account account) {
        this.transactionId = transactionId;
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.account = account;
    }


}

