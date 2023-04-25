package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Holiday;
import com.bigtreetc.sample.r2dbc.domain.model.HolidayCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HolidayQueryRepository {

  Mono<Holiday> findOne(final HolidayCriteria criteria);

  Mono<Page<Holiday>> findAll(final HolidayCriteria criteria, final Pageable pageable);

  Flux<Holiday> findAll(final HolidayCriteria criteria);
}
