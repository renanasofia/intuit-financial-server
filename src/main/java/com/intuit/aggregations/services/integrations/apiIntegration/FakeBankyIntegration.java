package com.intuit.aggregations.services.integrations.apiIntegration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.aggregations.controllers.domain.types.SourceType;
import com.intuit.aggregations.models.*;

import com.intuit.aggregations.services.http.Connection;
import com.intuit.aggregations.services.http.HttpResponse;
import com.intuit.aggregations.services.integrations.SourceExtractor;
import com.intuit.aggregations.services.integrations.apiIntegration.models.Account;
import com.intuit.aggregations.services.integrations.apiIntegration.models.FakyTransaction;
import com.intuit.aggregations.services.integrations.apiIntegration.models.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class FakeBankyIntegration extends SourceExtractor {

    @Value("${api.target}")
    private String target;

    private HttpResponse source;
    final private SourceType type = SourceType.API;
    private ObjectMapper mapper = new ObjectMapper();
    private Connection connection = new Connection();
    private static final Logger logger = LoggerFactory.getLogger(FakeBankyIntegration.class);

    @Override
    public void fetch() {
        logger.info(String.format("fetch info from source %s", target));
        source = connection.call(target);
    }

    @Override
    public UserSource transform() {
        List<UserAccount> accounts = new ArrayList<>();
        if (source.getSuccess()) {
            logger.info(String.format("success getting input from %s", target));
            logger.debug(String.format("input => %s", source.getResponse()));
            try {
                Source accs = mapper.readValue(source.getResponse(), Source.class);
                for (Account account: accs.getAccounts()) {
                    List<Transaction> transactions = new ArrayList<>();
                    for (FakyTransaction t: account.getTransactions()) {
                        transactions.add(new Transaction(t.getId(), t.getAmount(), t.getCurrency()));
                    }
                    accounts.add(new UserAccount(null, account.getAccount(), account.getType(), null, null, transactions));
                }

            } catch (JsonProcessingException e) {
                logger.error("failed to parse input from %s", e);
            }
        }
        return new UserSource(type, accounts);
    }
}
