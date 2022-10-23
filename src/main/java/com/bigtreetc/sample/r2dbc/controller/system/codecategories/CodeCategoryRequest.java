package com.bigtreetc.sample.r2dbc.controller.system.codecategories;

import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeCategoryRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // 分類コード
  @NotEmpty String categoryCode;

  // 分類名
  @NotEmpty String categoryName;

  // 改定番号
  Integer version;
}
