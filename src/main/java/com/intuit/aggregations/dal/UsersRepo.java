package com.intuit.aggregations.dal;

import com.intuit.aggregations.dal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

}
