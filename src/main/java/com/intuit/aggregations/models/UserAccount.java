package com.intuit.aggregations.models;

import com.intuit.aggregations.controllers.domain.types.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount {
    private String name;
    private String id;
    private String type;
    private BigDecimal balance;
    private Currency currency;
    private List<Transaction> transactions;

}
