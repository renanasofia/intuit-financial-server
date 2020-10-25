package com.intuit.aggregations.models;

import com.intuit.aggregations.controllers.domain.types.Currency;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String id;
    private Long date;
    private BigDecimal amount;
    private Currency currency;
    private String description;

    public Transaction(String id, BigDecimal amount, Currency currency) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
    }
}
