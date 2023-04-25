package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Permission;
import com.bigtreetc.sample.r2dbc.domain.model.PermissionCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PermissionQueryRepository {

  Mono<Permission> findOne(final PermissionCriteria criteria);

  Mono<Page<Permission>> findAll(final PermissionCriteria criteria, final Pageable pageable);

  Flux<Permission> findAll(final PermissionCriteria criteria);
}
