package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Holiday;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

/** 祝日マスタリポジトリ */
@Repository
public interface HolidayRepository
    extends ReactiveSortingRepository<Holiday, UUID>,
        ReactiveCrudRepository<Holiday, UUID>,
        ReactiveQueryByExampleExecutor<Holiday>,
        HolidayQueryRepository {}
