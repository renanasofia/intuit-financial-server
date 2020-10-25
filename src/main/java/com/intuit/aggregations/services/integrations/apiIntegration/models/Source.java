package com.intuit.aggregations.services.integrations.apiIntegration.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Class for JSON returned from API integration
 */
public class Source {
    List<Account> accounts;
}
