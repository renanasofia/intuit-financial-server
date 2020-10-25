package com.intuit.aggregations.controllers;

import com.intuit.aggregations.controllers.domain.models.AggregationReq;
import com.intuit.aggregations.models.UserSource;
import com.intuit.aggregations.services.SourcesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * Controller representing the Gateway service for all new aggregation requests
 */
@RestController
public class AggregationController {

    private SourcesFactory factory;

    @Autowired
    public AggregationController(SourcesFactory factory) {
        this.factory = factory;
    }

    private static final Logger logger = LoggerFactory.getLogger(AggregationController.class);

    @PostMapping("/users")
    public ResponseEntity<UserSource> getAggregateData(@Valid @RequestBody AggregationReq req) {
        logger.info(String.format("HTTP request => POST /aggregate/user/source for userId %d", req.getId()));
        UserSource res = factory.getDataBySource(req.getId(), req.getUsername(), req.getSource());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
