package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCategoryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CodeCategoryQueryRepository {

  Mono<CodeCategory> findOne(final CodeCategoryCriteria criteria);

  Mono<Page<CodeCategory>> findAll(final CodeCategoryCriteria criteria, final Pageable pageable);

  Flux<CodeCategory> findAll(final CodeCategoryCriteria criteria);
}
