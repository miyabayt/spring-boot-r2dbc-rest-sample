package com.bigtreetc.sample.r2dbc.domain.model.system;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isEquals;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("roles")
public class Role extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  // ロールID
  @Id UUID id;

  // ロールコード
  String roleCode;

  // ロール名
  String roleName;

  final @Transient List<RolePermission> rolePermissions = new ArrayList<>();

  final @Transient List<Permission> permissions = new ArrayList<>();

  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }

  public boolean hasPermission(String permissionCode) {
    return rolePermissions.stream()
        .anyMatch(rp -> isEquals(rp.getPermissionCode(), permissionCode) && rp.getIsEnabled());
  }
}
