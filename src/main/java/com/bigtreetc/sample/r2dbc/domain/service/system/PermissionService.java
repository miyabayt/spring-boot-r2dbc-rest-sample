package com.bigtreetc.sample.r2dbc.domain.service.system;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.system.Permission;
import com.bigtreetc.sample.r2dbc.domain.model.system.PermissionCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.system.PermissionRepository;
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

/** 権限サービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class PermissionService {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final PermissionRepository permissionRepository;

  /**
   * 権限を検索します。
   *
   * @param permission
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<Permission>> findAll(Permission permission, Pageable pageable) {
    Assert.notNull(permission, "permission must not be null");

    val criteria = new ArrayList<Criteria>();
    if (isNotEmpty(permission.getPermissionCode())) {
      criteria.add(where("permission_code").is(permission.getPermissionCode()));
    }
    if (isNotEmpty(permission.getPermissionName())) {
      criteria.add(
          where("permission_name").like("%%%s%%".formatted(permission.getPermissionName())));
    }

    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(Permission.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(r2dbcEntityTemplate.count(query, Permission.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  /**
   * 権限を取得します。
   *
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Permission> findOne(PermissionCriteria permission) {
    Assert.notNull(permission, "permission must not be null");
    return permissionRepository.findOne(Example.of(permission));
  }

  /**
   * 権限を取得します。
   *
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Permission> findById(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return permissionRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new NoDataFoundException("id=" + id + " のデータが見つかりません。")));
  }

  /**
   * 権限を登録します。
   *
   * @param permission
   * @return
   */
  public Mono<Permission> create(final Permission permission) {
    Assert.notNull(permission, "permission must not be null");
    permission.setId(UUID.randomUUID());
    return permissionRepository.save(permission);
  }

  /**
   * 権限を登録します。
   *
   * @param permissions
   * @return
   */
  public Flux<Permission> create(final List<Permission> permissions) {
    Assert.notNull(permissions, "permissions must not be null");
    for (val permission : permissions) {
      permission.setId(UUID.randomUUID());
    }
    return permissionRepository.saveAll(permissions);
  }

  /**
   * 権限を更新します。
   *
   * @param permission
   * @return
   */
  public Mono<Permission> update(final Permission permission) {
    Assert.notNull(permission, "permission must not be null");
    return permissionRepository.save(permission);
  }

  /**
   * 権限を更新します。
   *
   * @param permissions
   * @return
   */
  public Flux<Permission> update(final List<Permission> permissions) {
    Assert.notNull(permissions, "permission must not be null");
    return permissionRepository.saveAll(permissions);
  }

  /**
   * 権限を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return permissionRepository.deleteById(id);
  }

  /**
   * 権限を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return permissionRepository.deleteAllById(ids);
  }
}
