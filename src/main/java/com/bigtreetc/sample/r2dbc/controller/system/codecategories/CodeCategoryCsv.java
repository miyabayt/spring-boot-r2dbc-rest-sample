package com.bigtreetc.sample.r2dbc.controller.system.codecategories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true) // 定義されていないプロパティを無視してマッピングする
@JsonPropertyOrder({"コード分類ID", "分類コード", "分類名"}) // CSVのヘッダ順
@Getter
@Setter
public class CodeCategoryCsv implements Serializable {

  private static final long serialVersionUID = -1L;

  @JsonProperty("コード分類ID")
  UUID id;

  @JsonProperty("分類コード")
  String categoryCode;

  @JsonProperty("分類名")
  String categoryName;
}
