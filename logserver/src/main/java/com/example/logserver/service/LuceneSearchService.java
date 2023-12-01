package com.example.logserver.service;

import com.example.logserver.entity.Log;
import com.example.logserver.service.dto.LogRequest;
import com.example.logserver.service.dto.LogResponse;
import com.example.logserver.service.dto.SearchTimeType;
import io.micrometer.observation.Observation;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.*;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.data.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class LuceneSearchService {

    private final EntityManager em;

    private SearchSession searchSession;

    @PostConstruct
    public void init() throws InterruptedException {
        EntityManager entityManager = em.getEntityManagerFactory().createEntityManager();
        searchSession = Search.session(entityManager.unwrap(Session.class));
        searchSession.massIndexer().startAndWait();
    }


    public Page<LogResponse> search(LogRequest request) {

        PageRequest pageRequest = PageRequest.of(request.getPageNumber(), request.getPageSize());

        SearchScope<Log> scope = searchSession.scope(Log.class);

        BooleanPredicateClausesStep<?> booleanClauses = scope.predicate().bool();

        String serviceName = request.getService();
        String keyword = request.getKeyword();

        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("keyword must be required");
        }

        if (StringUtils.hasText(serviceName)) {
            booleanClauses.filter(
                    () -> scope.predicate().match().field("logger.service").matching(serviceName).toPredicate()
            );
        }

        MatchPredicateOptionsStep<?> matching = scope.predicate().match().field("log").matching(keyword);


        switch (request.getType()) {
            case FULL_TEXT -> booleanClauses.must(matching.toPredicate());
            case SIMPLE -> booleanClauses
                    .must(
                            () -> scope.predicate()
                                    .simpleQueryString()
                                    .field("log")
                                    .matching(keyword)
                                    .toPredicate()
                    );
            case FUZZY -> {
                if (request.getProps() == null) {
                    booleanClauses.must(matching.fuzzy().toPredicate());
                } else {
                    int maxEditDistance = request.getProps().getMaxEditDistance();
                    int exactPrefixLength = request.getProps().getExactPrefixLength();

                    booleanClauses.must(matching.fuzzy(maxEditDistance, exactPrefixLength).toPredicate());

                }
            }
            case PHRASE -> {
                PhrasePredicateOptionsStep<?> pharse = scope.predicate().phrase().field("log").matching(keyword);
                int slop = request.getProps().getSlop();
                if (slop != -1) {
                    booleanClauses.must(pharse.slop(slop).toPredicate());
                } else {
                    booleanClauses.must(pharse.toPredicate());
                }
            }
            case WILDCARD ->
                    booleanClauses.must(scope.predicate().wildcard().field("log").matching(keyword).toPredicate());
            case REGEXP ->
                    booleanClauses.must(scope.predicate().regexp().field("log").matching(keyword).flags(RegexpQueryFlag.values()).toPredicate());
            case TERM -> booleanClauses.must(scope.predicate().terms().field("log").matchingAny(keyword).toPredicate());
        }


        RangePredicateFieldMoreStep<?, ?> rangeQuery = scope.predicate().range().field("timestamp");

        if (!request.getSearchTime().getType().equals(SearchTimeType.NONE)) {
            switch (request.getSearchTime().getType()) {
                case BETWEEN -> booleanClauses.filter(
                        () -> rangeQuery
                                .range(Range.between(request.getSearchTime().getStartTimestamp(), request.getSearchTime().getEndTimestamp()))
                                .toPredicate()
                );
                case GRATER -> booleanClauses.filter(
                        () -> rangeQuery
                                .range(Range.atMost(request.getSearchTime().getStartTimestamp()))
                                .toPredicate()
                );
                case LESS -> booleanClauses.filter(
                        () -> rangeQuery
                                .range(Range.atLeast(request.getSearchTime().getEndTimestamp()))
                                .toPredicate()
                );
            }
        }


        SearchPredicate predicate = booleanClauses.toPredicate();

        SearchResult<LogResponse> fetch = searchSession.search(Log.class)
                .select(LogResponse.class)
                .where(predicate)
                .sort(scope.sort().field("timestamp").asc().toSort())
                .fetch((int) pageRequest.getOffset(), pageRequest.getPageSize());

        return new PageImpl<>(fetch.hits(), pageRequest, fetch.total().hitCount());

    }


}
