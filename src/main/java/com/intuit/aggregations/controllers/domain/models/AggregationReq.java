package com.intuit.aggregations.controllers.domain.models;

import com.intuit.aggregations.controllers.domain.types.SourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregationReq {
    @NotNull
    private Long id;

    @NotNull
    private SourceType source;

    @NotNull
    private String username;

}
