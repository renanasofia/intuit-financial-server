package com.intuit.aggregations.dal.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intuit.aggregations.controllers.domain.types.SourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sources", uniqueConstraints=@UniqueConstraint(columnNames = {"source", "user_id"}))
public class Source {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "source", nullable = false)
    private SourceType sourceType;

    @Column(name = "aggregationDate")
    private Long aggregationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    public Source(SourceType sourceType, Long now, User user) {
        this.sourceType = sourceType;
        this.aggregationDate = now;
        this.user = user;
        this.accounts = new ArrayList<>();
    }
}
