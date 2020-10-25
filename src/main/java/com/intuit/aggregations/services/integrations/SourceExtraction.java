package com.intuit.aggregations.services.integrations;

import com.intuit.aggregations.models.*;

/**
 * Interface for data extraction from sources
 */
public interface SourceExtraction {
    UserSource pullFromSource();
    void fetch();
    UserSource transform();
}
