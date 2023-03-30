package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.Role;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/** ロールマスタリポジトリ */
@Repository
public interface RoleRepository
    extends ReactiveSortingRepository<Role, UUID>,
        ReactiveCrudRepository<Role, UUID>,
        ReactiveQueryByExampleExecutor<Role>,
        RoleQueryRepository {

  Mono<Void> deleteByRoleCode(String roleCode);
}
