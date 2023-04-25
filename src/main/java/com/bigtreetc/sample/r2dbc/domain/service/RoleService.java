package com.bigtreetc.sample.r2dbc.domain.service;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.*;
import com.bigtreetc.sample.r2dbc.domain.repository.PermissionRepository;
import com.bigtreetc.sample.r2dbc.domain.repository.RolePermissionRepository;
import com.bigtreetc.sample.r2dbc.domain.repository.RoleRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** ロールマスタサービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class RoleService {

  @NonNull final RoleRepository roleRepository;

  @NonNull final RolePermissionRepository rolePermissionRepository;

  @NonNull final PermissionRepository permissionRepository;

  /**
   * ロールマスタを検索します。
   *
   * @param criteria
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<Role>> findAll(final RoleCriteria criteria, final Pageable pageable) {
    Assert.notNull(criteria, "criteria must not be null");
    Assert.notNull(pageable, "pageable must not be null");
    return roleRepository.findAll(criteria, pageable);
  }

  /**
   * ロールマスタを検索します。
   *
   * @param criteria
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Flux<Role> findAll(final RoleCriteria criteria) {
    Assert.notNull(criteria, "criteria must not be null");
    return roleRepository.findAll(criteria);
  }

  /**
   * ロールマスタを取得します。
   *
   * @param criteria
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Role> findOne(RoleCriteria criteria) {
    Assert.notNull(criteria, "criteria must not be null");
    return roleRepository
        .findOne(Example.of(criteria))
        .flatMap(this::getRolePermissions)
        .flatMap(this::getPermissions);
  }

  /**
   * ロールマスタを取得します。
   *
   * @param id
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Role> findById(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return roleRepository
        .findById(id)
        .flatMap(this::getRolePermissions)
        .flatMap(this::getPermissions)
        .switchIfEmpty(Mono.error(new NoDataFoundException("id=" + id + " のデータが見つかりません。")));
  }

  /**
   * ロールマスタを登録します。
   *
   * @param role
   * @return
   */
  public Mono<Role> create(final Role role) {
    Assert.notNull(role, "role must not be null");
    role.setId(UUID.randomUUID());
    return roleRepository
        .save(role)
        .flatMapMany(r -> Flux.fromIterable(r.getRolePermissions()))
        .collectList()
        .flatMapMany(rolePermissionRepository::saveAll)
        .collectList()
        .thenReturn(role);
  }

  /**
   * ロールマスタを登録します。
   *
   * @param roles
   * @return
   */
  public Flux<Role> create(final List<Role> roles) {
    Assert.notNull(roles, "roles must not be null");
    for (val role : roles) {
      role.setId(UUID.randomUUID());
    }
    return roleRepository.saveAll(roles);
  }

  /**
   * ロールマスタを更新します。
   *
   * @param role
   * @return
   */
  public Mono<Role> update(final Role role) {
    Assert.notNull(role, "role must not be null");
    return roleRepository
        .save(role)
        .flatMapMany(r -> Flux.fromIterable(r.getRolePermissions()))
        .collectList()
        .flatMapMany(rolePermissionRepository::saveAll)
        .collectList()
        .thenReturn(role);
  }

  /**
   * ロールマスタを更新します。
   *
   * @param roles
   * @return
   */
  public Flux<Role> update(final List<Role> roles) {
    Assert.notNull(roles, "role must not be null");
    return roleRepository.saveAll(roles);
  }

  /**
   * ロールマスタを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return roleRepository
        .findById(id)
        .flatMap(
            role -> {
              val roleCode = role.getRoleCode();
              return roleRepository
                  .deleteByRoleCode(roleCode)
                  .then(rolePermissionRepository.deleteByRoleCode(roleCode));
            });
  }

  /**
   * ロールマスタを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return roleRepository.deleteAllById(ids);
  }

  private Mono<Role> getRolePermissions(Role r) {
    return rolePermissionRepository
        .findByRoleCode(r.getRoleCode())
        .collectList()
        .map(
            rp -> {
              r.getRolePermissions().addAll(rp);
              return r;
            });
  }

  private Mono<Role> getPermissions(Role role) {
    val permissionCodes =
        role.getRolePermissions().stream().map(RolePermission::getPermissionCode).toList();
    return permissionRepository
        .findByPermissionCodeIn(permissionCodes)
        .collectList()
        .flatMap(
            p -> {
              role.getPermissions().addAll(p);
              return Mono.just(role);
            });
  }
}
