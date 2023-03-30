package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.User;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends ReactiveSortingRepository<User, UUID>,
        ReactiveCrudRepository<User, UUID>,
        ReactiveQueryByExampleExecutor<User>,
        UserQueryRepository {}
