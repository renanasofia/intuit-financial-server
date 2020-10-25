package com.intuit.aggregations.dal;

import com.intuit.aggregations.dal.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountsRepo extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
}
