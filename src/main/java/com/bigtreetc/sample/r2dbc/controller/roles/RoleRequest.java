package com.bigtreetc.sample.r2dbc.controller.roles;

import jakarta.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // ロールコード
  @NotEmpty String roleCode;

  // ロール名
  @NotEmpty String roleName;

  // 権限
  Map<String, Boolean> permissions = new HashMap<>();

  // 改定番号
  Integer version;
}
