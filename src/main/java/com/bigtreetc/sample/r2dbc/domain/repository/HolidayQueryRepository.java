package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.system.Holiday;
import com.bigtreetc.sample.r2dbc.domain.model.system.HolidayCriteria;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface HolidayQueryRepository {

  Mono<Page<Holiday>> findAll(final Example<HolidayCriteria> example, final Pageable pageable);
}
