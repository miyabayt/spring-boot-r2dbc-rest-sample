package com.bigtreetc.sample.r2dbc.controller.system.codes;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchCodeRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // 分類コード
  String categoryCode;

  // コード値
  String codeValue;

  // 改定番号
  Integer version;
}
