package com.bigtreetc.sample.r2dbc.domain.service.users;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.user.User;
import com.bigtreetc.sample.r2dbc.domain.model.user.UserCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.users.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** ユーザサービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserService {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final UserRepository userRepository;

  /**
   * ユーザを検索します。
   *
   * @param user
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<User>> findAll(final UserCriteria user, final Pageable pageable) {
    Assert.notNull(user, "user must not be null");

    val criteria = new ArrayList<Criteria>();
    if (isNotEmpty(user.getFirstName())) {
      criteria.add(where("first_name").like("%%%s%%".formatted(user.getFirstName())));
    }
    if (isNotEmpty(user.getLastName())) {
      criteria.add(where("last_name").like("%%%s%%".formatted(user.getLastName())));
    }
    if (isNotEmpty(user.getEmail())) {
      criteria.add(where("email").like("%%%s%%".formatted(user.getEmail())));
    }

    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(User.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(r2dbcEntityTemplate.count(query, User.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  /**
   * ユーザを取得します。
   *
   * @param user
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<User> findOne(UserCriteria user) {
    Assert.notNull(user, "criteria must not be null");
    return userRepository.findOne(Example.of(user));
  }

  /**
   * ユーザを取得します。
   *
   * @param id
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<User> findById(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return userRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new NoDataFoundException("id=" + id + " のデータが見つかりません。")));
  }

  /**
   * ユーザを登録します。
   *
   * @param user
   * @return
   */
  public Mono<User> create(final User user) {
    Assert.notNull(user, "user must not be null");
    user.setId(UUID.randomUUID());
    return userRepository.save(user);
  }

  /**
   * ユーザを登録します。
   *
   * @param users
   * @return
   */
  public Flux<User> create(final List<User> users) {
    Assert.notNull(users, "users must not be null");
    for (val user : users) {
      user.setId(UUID.randomUUID());
    }
    return userRepository.saveAll(users);
  }

  /**
   * ユーザを更新します。
   *
   * @param user
   * @return
   */
  public Mono<User> update(final User user) {
    Assert.notNull(user, "user must not be null");
    return userRepository.save(user);
  }

  /**
   * ユーザを更新します。
   *
   * @param users
   * @return
   */
  public Flux<User> update(final List<User> users) {
    Assert.notNull(users, "user must not be null");
    return userRepository.saveAll(users);
  }

  /**
   * ユーザを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return userRepository.deleteById(id);
  }

  /**
   * ユーザを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return userRepository.deleteAllById(ids);
  }
}
