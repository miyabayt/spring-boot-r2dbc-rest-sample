package com.bigtreetc.sample.r2dbc.domain.repository.system;

import com.bigtreetc.sample.r2dbc.domain.model.system.Staff;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StaffRepository
    extends ReactiveSortingRepository<Staff, UUID>, ReactiveQueryByExampleExecutor<Staff> {

  Mono<Staff> findByEmail(String email);
}
