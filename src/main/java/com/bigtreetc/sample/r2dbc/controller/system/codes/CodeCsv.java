package com.bigtreetc.sample.r2dbc.controller.system.codes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true) // 定義されていないプロパティを無視してマッピングする
@JsonPropertyOrder({
  "コードID",
  "コード分類コード",
  "コード分類名",
  "コードコード",
  "コード値",
  "コードエイリアス",
  "表示順",
  "無効フラグ"
}) // CSVのヘッダ順
@Getter
@Setter
public class CodeCsv implements Serializable {

  private static final long serialVersionUID = -1L;

  @JsonProperty("コードID")
  UUID id;

  @JsonProperty("コード分類コード")
  String categoryCode;

  @JsonProperty("コード分類名")
  String categoryName;

  @JsonProperty("コード値")
  String codeValue;

  @JsonProperty("コードエイリアス")
  String codeAlias;

  @JsonProperty("表示順")
  Integer displayOrder;

  @JsonProperty("無効フラグ")
  Boolean isInvalid;
}
