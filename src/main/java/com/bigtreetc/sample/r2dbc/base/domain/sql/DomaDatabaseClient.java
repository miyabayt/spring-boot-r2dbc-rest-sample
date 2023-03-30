package com.bigtreetc.sample.r2dbc.base.domain.sql;

import java.util.List;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface DomaDatabaseClient {

  <T> Mono<T> one(DomaSqlBuilder sqlBuilder, Class<T> type);

  <T> Mono<Tuple2<List<T>, Long>> all(DomaSqlBuilder sqlBuilder, Class<T> type);
}
