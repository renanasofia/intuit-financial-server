package com.intuit.aggregations.services.integrations.websiteIntegration;

import com.intuit.aggregations.controllers.domain.types.Currency;
import com.intuit.aggregations.controllers.domain.types.SourceType;
import com.intuit.aggregations.models.Transaction;

import com.intuit.aggregations.models.UserAccount;
import com.intuit.aggregations.models.UserSource;
import com.intuit.aggregations.services.integrations.SourceExtractor;
import com.intuit.aggregations.services.HtmlTransformer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;

@Component
public class FankyBankWebsite extends SourceExtractor {

    @Value("${website.target}")
    private String target;

    private final Logger logger = LoggerFactory.getLogger(FankyBankWebsite.class);
    private Document doc;
    final private SourceType type = SourceType.WEBSITE;
    static final int TIMEOUT = 60000;   // one minute


    @Override
    public void fetch() {
        logger.info(String.format("fetch web info from source %s", target));
        try {
            doc = Jsoup.parse(new URL(target), TIMEOUT);
        } catch (IOException e) {
            logger.error("failed to crawl html document", e.getMessage());
        }
    }

    @Override
    public UserSource transform() {
        logger.info(String.format("success getting input from %s", target));
        List<UserAccount> ua = new ArrayList<>();
        ua.add(extractUserAccountInfo());
        return new UserSource(type, ua);

    }

    private UserAccount extractUserAccountInfo() {
        UserAccount userAccount = new UserAccount();
        logger.debug(String.format("input => attempting to convert html to properties"));
        try {
            Elements userData = doc.selectFirst("h1:contains(user data)").parent().select(":root > div");
            userAccount.setName(extractAccountName(userData.get(0)));
            userAccount.setBalance(extractBalance(userData.get(1).select(":root > div").get(0)));
            userAccount.setCurrency(extractCurrency(userData.get(1).select(":root > div").get(0)));
            userAccount.setId(extractAccountId(userData.get(1).select(":root > div").get(1)));
            userAccount.setTransactions(extractTransactionsInfo(userData.get(2)));
        } catch (Exception e) {
            logger.error("failed to parse html to properties", e.getMessage());
        }
        return userAccount;
    }
    private Currency extractCurrency(Element doc) {
        Elements trans = doc.select(":root > div");
        return HtmlTransformer.extractCurrency(trans.get(1).text());
    }

    private BigDecimal extractBalance(Element doc) {
        Elements trans = doc.select(":root > div");
        return HtmlTransformer.extractBalance(trans.get(1).text());
    }
    private String extractAccountId(Element doc) {
        return doc.child(1).text();
    }

    private String extractAccountName(Element doc) {
        return doc.child(0).text();
    }

    private Long extractDate(Element doc) {
        return HtmlTransformer.extractDate(doc.child(1).text());
    }

    private List<Transaction> extractTransactionsInfo(Element doc) {
        List<Transaction> transactions = new ArrayList<>();
        Elements trans = doc.select(":root > div");
        for (Element prop: trans) {
            Transaction transaction = new Transaction();
            Elements props = prop.select(":root > div");
            for (Element val: props) {
                ArrayList<Element> pair = val.select(":root > div");
                String key = pair.get(0).text();
                String value = pair.get(1).text();
                setTransactionProps(transaction, key.toLowerCase(), value);
            }
            transactions.add(transaction);
        }
        return transactions;
    }

    private void setTransactionProps(Transaction transaction, String key, String value) {
        switch(key) {
            case "date":
                transaction.setDate(HtmlTransformer.extractDate(value));
                break;
            case "blance":
            case "balance":
                transaction.setAmount(HtmlTransformer.extractBalance(value));
                transaction.setCurrency(HtmlTransformer.extractCurrency(value));
                break;
            case "description":
                transaction.setDescription(value);
                break;
            case "id":
                transaction.setId(value);
                break;
        }
    }

}
