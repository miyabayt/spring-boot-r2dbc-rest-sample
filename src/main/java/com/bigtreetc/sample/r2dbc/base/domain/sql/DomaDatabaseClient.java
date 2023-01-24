package com.bigtreetc.sample.r2dbc.base.domain.sql;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DomaDatabaseClient {

  <T> Mono<T> one(DomaSqlBuilder sqlBuilder, Class<T> type);

  <T> Flux<T> all(DomaSqlBuilder sqlBuilder, Class<T> type);
}
