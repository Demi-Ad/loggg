package com.example.logserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "logger", indexes = {@Index(name = "service_idx",columnList = "service")})
@EntityListeners(AuditingEntityListener.class)
public class Logger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequence;


    @KeywordField(name = "service", projectable = Projectable.YES, normalizer = "keyword_normalizer")
    @Column(name = "service", unique = true)
    private String service;

    @OneToMany(mappedBy = "logger", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Log> logList = new ArrayList<>();

    @Column(name = "created_at",updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public Logger(String service) {
        this.service = service;
    }


}
