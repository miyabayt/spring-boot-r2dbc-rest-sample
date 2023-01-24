package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.RolePermission;
import com.bigtreetc.sample.r2dbc.domain.model.RolePermissionCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface RolePermissionQueryRepository {

  Mono<RolePermission> findOne(final RolePermissionCriteria criteria);

  Mono<Page<RolePermission>> findAll(
      final RolePermissionCriteria criteria, final Pageable pageable);
}
