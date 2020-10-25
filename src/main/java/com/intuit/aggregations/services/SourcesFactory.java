package com.intuit.aggregations.services;

import com.intuit.aggregations.controllers.domain.types.SourceType;
import com.intuit.aggregations.controllers.errorHandlers.AggregationIntervalException;
import com.intuit.aggregations.controllers.errorHandlers.InvalidSourceTypeException;
import com.intuit.aggregations.models.*;
import com.intuit.aggregations.dal.models.Source;
import com.intuit.aggregations.services.integrations.SourceExtractor;
import com.intuit.aggregations.services.integrations.apiIntegration.FakeBankyIntegration;
import com.intuit.aggregations.services.integrations.websiteIntegration.FankyBankWebsite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SourcesFactory {

    @Value("${api.interval}")
    private int apiInterval;

    @Value("${website.interval}")
    private int websiteInterval;

    @Value("${millis-in-an-hour}")
    private int MILLIS_IN_AN_HOUR;

    private FakeBankyIntegration apiSource;
    private FankyBankWebsite webSource;
    private PersistenceService ps;
    private static final Logger logger = LoggerFactory.getLogger(SourcesFactory.class);

    @Autowired
    public SourcesFactory(FakeBankyIntegration apiSource, FankyBankWebsite webSource, PersistenceService ps) {
        this.apiSource = apiSource;
        this.webSource = webSource;
        this.ps = ps;
    }

    /**
     * Method to the fetchs data from source and converts to the UserSource.class
     * @param userId
     * @param username
     * @param sourceType
     * @return UserSource
     * @throws AggregationIntervalException if not enough time has passed from users last aggregation request for sourceType
     */
    public UserSource getDataBySource(Long userId, String username, SourceType sourceType) {
        logger.info(String.format("Aggregate %s by userId %d" , sourceType.toString(), userId));
        Optional<Source> s = ps.getUserSource(userId, sourceType);
        Long now = System.currentTimeMillis();
        if (s.isPresent() && !isValidToContinue(sourceType, now, s.get().getAggregationDate())) {
            logger.warn(String.format("user %d is blocked from downloading source %s => not enough time has passed since last aggregation at %d",
                    userId, sourceType.toString(), s.get().getAggregationDate()));
            throw new AggregationIntervalException(String.format("The interval for Source %s requested by UserId %d is not complete", sourceType.toString(),userId));
        }
        SourceExtractor extractor = getExtractor(sourceType);
        UserSource data = extractor.pullFromSource();
        ps.persistData(data, now, userId, username);
        return data;

    }

    private SourceExtractor getExtractor(SourceType sourceType) {
        logger.debug(String.format("init extractor of type %s", sourceType.toString()));
        if (sourceType.equals(SourceType.WEBSITE)) {
            return webSource;
        } else if (sourceType.equals(SourceType.API)) {
            return apiSource;
        }
        logger.error(String.format("%s is unsupported source type", sourceType.toString()));
        throw new InvalidSourceTypeException(String.format("%s is unsupported source type", sourceType.toString()));
    }

    private boolean isValidToContinue(SourceType sourceType, Long now, Long lastAggregationTime) {
        Long nextValidPullTime = lastAggregationTime;
        if (sourceType.equals(SourceType.WEBSITE)) {
            nextValidPullTime += (websiteInterval * MILLIS_IN_AN_HOUR);
        } else if (sourceType.equals(SourceType.API)) {
            nextValidPullTime += (apiInterval * MILLIS_IN_AN_HOUR);
        }
        return (nextValidPullTime <= now);
    }


}
