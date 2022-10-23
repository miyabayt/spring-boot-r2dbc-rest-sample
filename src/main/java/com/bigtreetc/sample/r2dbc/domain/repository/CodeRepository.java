package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Code;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

/** コード定義リポジトリ */
@Repository
public interface CodeRepository
    extends ReactiveSortingRepository<Code, UUID>,
        ReactiveQueryByExampleExecutor<Code>,
        CodeQueryRepository {}
