package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Role;
import com.bigtreetc.sample.r2dbc.domain.model.RoleCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleQueryRepository {

  Mono<Role> findOne(final RoleCriteria criteria);

  Mono<Page<Role>> findAll(final RoleCriteria criteria, final Pageable pageable);

  Flux<Role> findAll(final RoleCriteria criteria);
}
