package com.intuit.aggregations.dal;

import com.intuit.aggregations.dal.models.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TransactionsRepo extends JpaRepository<TransactionModel, Long>, JpaSpecificationExecutor<TransactionModel> {
    List<TransactionModel> findByAccountId(Long accountId);
}
