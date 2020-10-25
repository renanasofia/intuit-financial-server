package com.intuit.aggregations.controllers;

import com.intuit.aggregations.controllers.errorHandlers.NotFoundException;
import com.intuit.aggregations.dal.models.User;
import com.intuit.aggregations.services.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller representing the Gateway service for all new persisted data requests
 */
@RestController
@RequestMapping("/users")
public class RetrievingController {

    private PersistenceService persist;

    @Autowired
    public RetrievingController(PersistenceService persist) {
        this.persist = persist;
    }

    private static final Logger logger = LoggerFactory.getLogger(RetrievingController.class);

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info(String.format("HTTP request => GET /users/%d", id));
        Optional<User> optAccount = persist.getUserById(id);
        if(optAccount.isPresent()) { //
            User res =  optAccount.get();
            return new ResponseEntity<>(res, HttpStatus.OK);
        }else {
            logger.warn(String.format("User %s not found", id));
            throw new NotFoundException(String.format("User %s not found", id));
        }
    }
}
