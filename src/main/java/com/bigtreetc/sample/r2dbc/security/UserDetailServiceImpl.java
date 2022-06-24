package com.bigtreetc.sample.r2dbc.security;

import com.bigtreetc.sample.r2dbc.domain.model.system.RolePermission;
import com.bigtreetc.sample.r2dbc.domain.model.system.StaffRole;
import com.bigtreetc.sample.r2dbc.domain.repository.system.RolePermissionRepository;
import com.bigtreetc.sample.r2dbc.domain.repository.system.StaffRepository;
import com.bigtreetc.sample.r2dbc.domain.repository.system.StaffRoleRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserDetailServiceImpl implements ReactiveUserDetailsService {

  @NonNull final StaffRepository staffRepository;

  @NonNull final StaffRoleRepository staffRoleRepository;

  @NonNull final RolePermissionRepository rolePermissionRepository;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return staffRepository
        .findByEmail(username)
        .switchIfEmpty(
            Mono.error(
                new UsernameNotFoundException("no account found. [username=" + username + "]")))
        .flatMap(
            staff ->
                Mono.just(staff)
                    .zipWith(
                        staffRoleRepository
                            .findByStaffId(staff.getId())
                            .collectList()
                            .map(this::mapToRoleCodes)
                            .flatMap(this::getRolePermissions)
                            .flatMap(
                                rolePermissions -> {
                                  val roleCodes =
                                      rolePermissions.stream()
                                          .map(RolePermission::getRoleCode)
                                          .toList();
                                  val permissionCodes =
                                      rolePermissions.stream()
                                          .map(RolePermission::getPermissionCode)
                                          .toList();
                                  return Mono.just(roleCodes).zipWith(Mono.just(permissionCodes));
                                })
                            .map(
                                tuple2 -> {
                                  Set<String> authorities = new HashSet<>();
                                  authorities.addAll(tuple2.getT1());
                                  authorities.addAll(tuple2.getT2());
                                  return AuthorityUtils.createAuthorityList(
                                      authorities.toArray(new String[0]));
                                })))
        .map(
            tuple2 -> {
              val staffId = Objects.requireNonNull(tuple2.getT1().getId()).toString();
              val password = tuple2.getT1().getPassword();
              val authorityList = tuple2.getT2();
              return User.withUsername(staffId)
                  .password(password)
                  .authorities(authorityList)
                  .build();
            });
  }

  private Mono<List<RolePermission>> getRolePermissions(List<String> roleCodes) {
    return rolePermissionRepository.findByRoleCodeIn(roleCodes).collectList();
  }

  private List<String> mapToRoleCodes(List<StaffRole> staffRoles) {
    return staffRoles.stream().map(StaffRole::getRoleCode).distinct().toList();
  }
}
