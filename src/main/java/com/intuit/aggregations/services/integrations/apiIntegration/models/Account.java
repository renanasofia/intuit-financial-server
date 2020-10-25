package com.intuit.aggregations.services.integrations.apiIntegration.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * Account info for JSON returned from API integration
 */
public class Account {
    String account;
    String type;
    List<FakyTransaction> transactions;
}
