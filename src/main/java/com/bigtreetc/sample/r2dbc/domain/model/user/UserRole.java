package com.bigtreetc.sample.r2dbc.domain.model.user;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("user_roles")
public class UserRole extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  // 担当者ロールID
  @Id UUID id;

  // ユーザID
  Long userId;

  // ロールコード
  String roleCode;

  // ロール名
  String roleName;

  // 権限コード
  String permissionCode;

  // 権限名
  String permissionName;

  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }
}
