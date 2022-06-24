package com.bigtreetc.sample.r2dbc.domain.model.system;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("permissions")
public class Permission extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  // 権限ID
  @Id UUID id;

  // 権限コード
  String permissionCode;

  // 権限名
  String permissionName;

  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }
}
