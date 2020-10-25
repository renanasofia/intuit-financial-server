package com.intuit.aggregations.dal;

import com.intuit.aggregations.controllers.domain.types.SourceType;
import com.intuit.aggregations.dal.models.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface SourcesRepo extends JpaRepository<Source, Long>, JpaSpecificationExecutor<Source> {
    @Modifying
    @Transactional
    @Query(" UPDATE Source sr SET sr.aggregationDate=?1 where sr.sourceType = ?2 and sr.user.id = ?3")
    Source setAggregateDate(Long now, SourceType st, Long userId);
}

