package com.bigtreetc.sample.r2dbc.domain.repository.system;

import com.bigtreetc.sample.r2dbc.domain.model.system.Code;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

/** コードリポジトリ */
@Repository
public interface CodeRepository
    extends ReactiveSortingRepository<Code, UUID>, ReactiveQueryByExampleExecutor<Code> {}
