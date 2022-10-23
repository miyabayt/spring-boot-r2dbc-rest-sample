package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Staff;
import com.bigtreetc.sample.r2dbc.domain.model.StaffCriteria;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface StaffQueryRepository {

  Mono<Page<Staff>> findAll(final Example<StaffCriteria> example, final Pageable pageable);
}
