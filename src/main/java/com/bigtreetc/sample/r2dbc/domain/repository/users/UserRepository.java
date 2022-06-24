package com.bigtreetc.sample.r2dbc.domain.repository.users;

import com.bigtreetc.sample.r2dbc.domain.model.user.User;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends ReactiveSortingRepository<User, UUID>, ReactiveQueryByExampleExecutor<User> {}
