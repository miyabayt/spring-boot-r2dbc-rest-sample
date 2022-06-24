package com.bigtreetc.sample.r2dbc.domain.repository.system;

import com.bigtreetc.sample.r2dbc.domain.model.system.Role;
import java.util.*;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/** ロールリポジトリ */
@Repository
public interface RoleRepository
    extends ReactiveSortingRepository<Role, UUID>, ReactiveQueryByExampleExecutor<Role> {

  Mono<Void> deleteByRoleCode(String roleCode);
}
