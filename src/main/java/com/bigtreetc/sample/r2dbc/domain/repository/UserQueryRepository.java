package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.user.User;
import com.bigtreetc.sample.r2dbc.domain.model.user.UserCriteria;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface UserQueryRepository {

  Mono<Page<User>> findAll(final Example<UserCriteria> example, final Pageable pageable);
}
