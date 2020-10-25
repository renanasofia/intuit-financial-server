package com.intuit.aggregations.services;

import com.intuit.aggregations.controllers.domain.types.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * Class to convert text extracted from HTML documents into param
 */
public class HtmlTransformer {

    static private Pattern dollarPattern = Pattern.compile(".*usd.*|.*dolar.*|.*dollar.*|.*\\$.*");
    static private Pattern israelPattern = Pattern.compile(".*nis.*|.*israel.*");
    static private Pattern mexicoPattern = Pattern.compile(".*peso.*|.*mx.*");
    private static final Logger logger = LoggerFactory.getLogger(HtmlTransformer.class);

    /**
     * Extract decimal value from String
     * @param str - The decimal value as String
     * @return BigDecimal
     */
    static public BigDecimal extractBalance(String str) {
        try {
            final NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
            if (format instanceof DecimalFormat) {
                ((DecimalFormat) format).setParseBigDecimal(true);
            }
            return (BigDecimal) format.parse(str.replaceAll("[^\\d.,-]",""));
        } catch (ParseException e) {
            logger.error(format("failed to extract balance from string: %s", str), e.getCause());
        }
        return null;
    }

    /**
     * Extract Currency type value from String
     * @param str thr currency string.
     * @return Currency (enum)
     */
    static public Currency extractCurrency(String str) {
        String s = str.toLowerCase();
        if ( dollarPattern.matcher(s).matches()) {
            return Currency.USD;
        } else if (mexicoPattern.matcher(s).matches()) {
            return Currency.PESO;
        } else if (israelPattern.matcher(s).matches()) {
            return Currency.NIS;
        }
        logger.error(format("failed to extract currency from string %s", s));
        return null;

    }

    // TODO: Handle more time formats.
    // TODO: Consider using format as config/ user preference.
    /**
     * Extract timestamp from a String representing a date
     * method first trys to parse the String into a SimpleDateFormat
     * @param str the date string
     * @return Long
     */
    static public Long extractDate(String str) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MMM-yyyy").parse(str);
        } catch (ParseException e) {
            logger.error(format("failed to extract date from string: %s", str), e.getCause());
        }
        if (date!=null) return date.getTime();

        return null;
    }

}
