package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Staff;
import com.bigtreetc.sample.r2dbc.domain.model.StaffCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StaffQueryRepository {

  Mono<Staff> findOne(final StaffCriteria criteria);

  Mono<Page<Staff>> findAll(final StaffCriteria criteria, final Pageable pageable);

  Flux<Staff> findAll(final StaffCriteria criteria);
}
