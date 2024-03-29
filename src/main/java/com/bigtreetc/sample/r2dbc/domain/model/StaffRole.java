package com.bigtreetc.sample.r2dbc.domain.model;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("staff_roles")
public class StaffRole extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  // 担当者ロールID
  @Id UUID id;

  // 担当者ID
  UUID staffId;

  // ロールコード
  String roleCode;

  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }
}
