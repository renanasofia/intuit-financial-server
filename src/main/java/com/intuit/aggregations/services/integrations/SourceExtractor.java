package com.intuit.aggregations.services.integrations;

import com.intuit.aggregations.models.UserSource;
import lombok.Data;

@Data
abstract public class SourceExtractor implements SourceExtraction {
    protected String target;

    public UserSource pullFromSource() {
        fetch();
        return transform();
    }

    /**
     * Fetches data from source
     */
    public abstract void fetch();

    /**
     * Normalizes data into the UserSource
     * @return
     */
    public abstract UserSource transform();
}
