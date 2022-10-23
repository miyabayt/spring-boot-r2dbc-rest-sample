package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.CodeCategory;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

/** コード分類リポジトリ */
public interface CodeCategoryRepository
    extends ReactiveSortingRepository<CodeCategory, UUID>,
        ReactiveQueryByExampleExecutor<CodeCategory>,
        CodeCategoryQueryRepository {

  Mono<CodeCategory> findByCategoryCode(String categoryCode);
}
