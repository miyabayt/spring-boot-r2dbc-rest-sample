package com.bigtreetc.sample.r2dbc.domain.repository.system;

import com.bigtreetc.sample.r2dbc.domain.model.system.Holiday;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

/** 祝日リポジトリ */
@Repository
public interface HolidayRepository
    extends ReactiveSortingRepository<Holiday, UUID>, ReactiveQueryByExampleExecutor<Holiday> {}
