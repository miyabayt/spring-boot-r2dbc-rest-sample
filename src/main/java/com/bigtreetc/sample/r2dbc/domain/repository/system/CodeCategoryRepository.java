package com.bigtreetc.sample.r2dbc.domain.repository.system;

import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCategory;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/** コード分類リポジトリ */
@Repository
public interface CodeCategoryRepository
    extends ReactiveSortingRepository<CodeCategory, UUID>,
        ReactiveQueryByExampleExecutor<CodeCategory> {

  Mono<CodeCategory> findByCategoryCode(String categoryCode);
}
