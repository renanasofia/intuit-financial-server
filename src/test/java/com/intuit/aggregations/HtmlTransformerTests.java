package com.intuit.aggregations;

import com.intuit.aggregations.controllers.domain.types.Currency;
import com.intuit.aggregations.services.HtmlTransformer;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class HtmlTransformerTests {

    @Test
    public void testExtractCurrency() {
        Currency usd1 =  HtmlTransformer.extractCurrency("$100,00");
        Assert.assertTrue(usd1 != null && usd1.equals(Currency.USD));

        Currency usd2 =  HtmlTransformer.extractCurrency("100,00");
        Assert.assertFalse(usd2 != null &&usd2.equals(Currency.USD));

        Currency usd3 =  HtmlTransformer.extractCurrency("$ 100,00");
        Assert.assertTrue(usd3.equals(Currency.USD));

        Currency usd4 =  HtmlTransformer.extractCurrency("USD 100,00");
        Assert.assertTrue(usd4.equals(Currency.USD));

        Currency usd5 =  HtmlTransformer.extractCurrency("Dollar 100,00");
        Assert.assertTrue(usd5.equals(Currency.USD));

        Currency usd6 =  HtmlTransformer.extractCurrency(" 100,00 dollar");
        Assert.assertTrue(usd6.equals(Currency.USD));
    }

    @Test
    public void testExtractBalance() {
        BigDecimal basicResult = new BigDecimal(10000);

        BigDecimal usd1 =  HtmlTransformer.extractBalance("$100,00");
        Assert.assertTrue(usd1.equals(basicResult));


        BigDecimal usd2 =  HtmlTransformer.extractBalance("100,00");
        Assert.assertTrue(usd2.equals(basicResult));

        BigDecimal usd3 =  HtmlTransformer.extractBalance("$ 100.34");
        Assert.assertTrue(usd3.equals(BigDecimal.valueOf(100.34D)));

        BigDecimal usd4 =  HtmlTransformer.extractBalance("USD 10000");
        Assert.assertTrue(usd4.equals(basicResult));

        BigDecimal usd5 =  HtmlTransformer.extractBalance("Dollar 100,00");
        Assert.assertTrue(usd5.equals(basicResult));
    }

}
