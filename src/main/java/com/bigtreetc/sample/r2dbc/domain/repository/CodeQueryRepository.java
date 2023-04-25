package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Code;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CodeQueryRepository {

  Mono<Code> findOne(final CodeCriteria criteria);

  Mono<Page<Code>> findAll(final CodeCriteria criteria, final Pageable pageable);

  Flux<Code> findAll(final CodeCriteria criteria);
}
