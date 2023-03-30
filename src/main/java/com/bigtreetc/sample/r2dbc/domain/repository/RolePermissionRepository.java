package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.RolePermission;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RolePermissionRepository
    extends ReactiveSortingRepository<RolePermission, UUID>,
        ReactiveCrudRepository<RolePermission, UUID>,
        ReactiveQueryByExampleExecutor<RolePermission>,
        RolePermissionQueryRepository {

  Flux<RolePermission> findByRoleCode(String roleCode);

  Flux<RolePermission> findByRoleCodeIn(List<String> roleCodes);

  Mono<Void> deleteByRoleCode(String roleCode);
}
