package com.intuit.aggregations.models;

import com.intuit.aggregations.controllers.domain.types.SourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSource {
    private SourceType sourceType;
    private List<UserAccount> accounts;

}
