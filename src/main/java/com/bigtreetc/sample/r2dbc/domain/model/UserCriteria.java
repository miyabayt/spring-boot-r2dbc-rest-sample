package com.bigtreetc.sample.r2dbc.domain.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCriteria extends User {

  private static final long serialVersionUID = -1L;

  // ユーザID（複数指定）
  List<String> ids;

  // 住所がNULLのデータに絞り込む
  Boolean onlyNullAddress;
}
