package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.StaffRole;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface StaffRoleRepository
    extends ReactiveSortingRepository<StaffRole, UUID>, ReactiveQueryByExampleExecutor<StaffRole> {

  Flux<StaffRole> findByStaffId(UUID staffId);
}
