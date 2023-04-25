package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.User;
import com.bigtreetc.sample.r2dbc.domain.model.UserCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserQueryRepository {

  Mono<User> findOne(final UserCriteria criteria);

  Mono<Page<User>> findAll(final UserCriteria criteria, final Pageable pageable);

  Flux<User> findAll(final UserCriteria criteria);
}
