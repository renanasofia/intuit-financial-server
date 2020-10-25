package com.intuit.aggregations.services;

import com.intuit.aggregations.controllers.domain.types.SourceType;
import com.intuit.aggregations.dal.AccountsRepo;
import com.intuit.aggregations.dal.SourcesRepo;
import com.intuit.aggregations.dal.TransactionsRepo;
import com.intuit.aggregations.dal.UsersRepo;
import com.intuit.aggregations.models.*;
import com.intuit.aggregations.dal.models.Account;
import com.intuit.aggregations.dal.models.Source;
import com.intuit.aggregations.dal.models.TransactionModel;
import com.intuit.aggregations.dal.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;

@Service
public class PersistenceService {

    private SourcesRepo sourcesRepo;
    private TransactionsRepo transactionsRepo;
    private AccountsRepo accountsRepo;
    private UsersRepo usersRepo;
    private static final Logger logger = LoggerFactory.getLogger(PersistenceService.class);

    @Autowired
    public PersistenceService(SourcesRepo sourcesRepo, TransactionsRepo transactionsRepo, AccountsRepo accountsRepo, UsersRepo usersRepo) {
        this.sourcesRepo = sourcesRepo;
        this.transactionsRepo = transactionsRepo;
        this.accountsRepo = accountsRepo;
        this.usersRepo = usersRepo;
    }

    public Optional<Source> getUserSource(Long userId, SourceType sourceType) {
        Specification<Source> userP = Specification.where((row, cq, cb) -> cb.equal(row.get("user").get("id"), userId));
        Specification<Source> sourceP = Specification.where((row, cq, cb) -> cb.equal(row.get("sourceType"), sourceType));
        Specification<Source> predicate = userP.and(sourceP);
        return sourcesRepo.findOne(predicate);
    }

    public Optional<Account> getUserAccount(Long userId, String accountId) {
        logger.debug(String.format("get user account => userId %d; accountId %s", userId, accountId));
        Specification<Account> userP = Specification.where((row, cq, cb) -> cb.equal(row.get("source").get("user").get("id"), userId));
        Specification<Account> sourceP = Specification.where((row, cq, cb) -> cb.equal(row.get("accountId"), accountId));
        Specification<Account> predicate = userP.and(sourceP);
        return accountsRepo.findOne(predicate);
    }

    /**
     * Saves User / Source / Account / Transactions in DB
     * @param data
     * @param now
     * @param userId
     * @param username
     */
    public void persistData(UserSource data, Long now, Long userId, String username) {
        logger.info(String.format("Save %s source data for user %d", data.getSourceType().toString(), userId));
        User user = insertOrUpdateUser(userId, username);
        Source sr = insertOrUpdateSource(data, now, user);
        for (UserAccount acc: data.getAccounts()) {
            Account account = insertOrUpdateAccount(acc, sr);
            for (Transaction t: acc.getTransactions()) {
                insertOrUpdateTransaction(t, account);
            }
        }
    }

    public Optional<User> getUserById(Long id) {
        Specification<User> predicate = Specification.where((row, cq, cb) -> cb.equal(row.get("id"), id));
        return usersRepo.findOne(predicate);
    }

    /**
     * Insert or Updates AccountRow
     * @param acc
     * @param src
     * @return AccountRow
     */
    private Account insertOrUpdateAccount(UserAccount acc, Source src) {
        Optional<Account> acOpt = getUserAccount(src.getUser().getId(), acc.getId());
        Account account = null;
        if (acOpt.isPresent()) {
            logger.debug(String.format("=> account %s already exists", acc.getId()));
            account = acOpt.get();
        } else {
            logger.debug(String.format("=> new account %s; insert", acc.getId()));
            account = new Account(acc.getId(), acc.getName(), acc.getBalance(), acc.getCurrency(), src);
            accountsRepo.save(account);
        }
        return account;
    }

    /**
     * Insert TransactionRow if doesn't exist
     * @param t
     * @param account
     * @return
     */
    private void insertOrUpdateTransaction(Transaction t, Account account) {
        Specification<TransactionModel> tP = Specification.where((row, cq, cb) -> cb.equal(row.get("transactionId"), t.getId()));
        Specification<TransactionModel> accountP = Specification.where((row, cq, cb) -> cb.equal(row.get("account").get("id"), account.getId()));
        Optional<TransactionModel> tOpt = transactionsRepo.findOne(tP.and(accountP));
        if (!tOpt.isPresent()) {
            logger.debug(String.format("=> new transaction line for source %s user %d account %s; insert",
                    account.getSource().getSourceType().toString(), account.getSource().getUser().getId(), account.getAccountId()));
            TransactionModel tr = new TransactionModel(t.getId(), t.getDate(), t.getAmount(), t.getCurrency(), t.getDescription(), account);
            transactionsRepo.save(tr);
        } else {
            logger.debug(String.format("=> transaction line already saved; skipping"));
        }
    }

    /**
     * Insert SourceRow if doesn't exist, Updates aggregationTime if already exists
     * @param data
     * @param now
     * @param user
     * @return SourceRow
     */
    private Source insertOrUpdateSource(UserSource data, Long now, User user) {
        Optional<Source> srOpt = getUserSource(user.getId(), data.getSourceType());
        Source sr = null;
        if (srOpt.isPresent()) {
            logger.debug(String.format("=> source %s for user %d already exists; updating aggregation time", data.getSourceType().toString(),user.getId()));
            sr = srOpt.get();
            sr.setAggregationDate(now);
        } else {
            logger.debug(String.format("=> new source %s for user %d; insert", data.getSourceType().toString(),user.getId()));
            sr = new Source(data.getSourceType(), now, user);
        }
        sourcesRepo.save(sr);
        return sr;
    }

    /**
     * Insert UserRow if doesn't exist
     * @param userId
     * @param username
     * @returnmUserRow
     */
    private User insertOrUpdateUser(Long userId, String username) {
        Optional<User> optUser = usersRepo.findById(userId);
        User user = null;
        if (optUser.isPresent()) {
            logger.debug(String.format("=> user %d already exists", userId));
            user = optUser.get();
        } else {
            logger.debug(String.format("=> new user %d; insert", userId));
            user = usersRepo.save(new User(userId, username));
        }
        return user;
    }

}
