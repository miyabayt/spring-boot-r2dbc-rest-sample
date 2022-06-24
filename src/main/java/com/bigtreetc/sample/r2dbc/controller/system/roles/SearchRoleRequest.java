package com.bigtreetc.sample.r2dbc.controller.system.roles;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchRoleRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // ロールコード
  String roleCode;

  // ロール名
  String roleName;
}
