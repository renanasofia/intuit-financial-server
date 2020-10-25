package com.intuit.aggregations.services.integrations.apiIntegration.models;

import com.intuit.aggregations.controllers.domain.types.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
/**
 * Transaction info for JSON returned from API integration
 */
public class FakyTransaction {
    String id;
    BigDecimal amount;
    Currency currency;
}
