package com.bigtreetc.sample.r2dbc.controller.codes;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // 分類コード
  @NotEmpty String categoryCode;

  // コード名
  @NotEmpty String codeValue;

  // コード名
  String codeName;

  // コードエイリアス
  String codeAlias;

  // 表示順
  @NotNull Integer displayOrder;

  // 無効フラグ
  Boolean isInvalid;

  // 改定番号
  Integer version;
}
