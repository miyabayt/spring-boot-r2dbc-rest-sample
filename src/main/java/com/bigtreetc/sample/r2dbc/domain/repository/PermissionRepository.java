package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Permission;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/** 権限リポジトリ */
@Repository
public interface PermissionRepository
    extends ReactiveSortingRepository<Permission, UUID>,
        ReactiveCrudRepository<Permission, UUID>,
        ReactiveQueryByExampleExecutor<Permission>,
        PermissionQueryRepository {

  Flux<Permission> findByPermissionCodeIn(List<String> permissionCodes);
}
