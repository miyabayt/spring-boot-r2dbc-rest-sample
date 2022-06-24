package com.bigtreetc.sample.r2dbc.controller.system.roles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true) // 定義されていないプロパティを無視してマッピングする
@JsonPropertyOrder({"ロールID", "ロールコード", "ロール名"}) // CSVのヘッダ順
@Getter
@Setter
public class RoleCsv implements Serializable {

  private static final long serialVersionUID = -1L;

  @JsonProperty("ロールID")
  UUID id;

  @JsonProperty("ロールコード")
  String roleCode;

  @JsonProperty("ロール名")
  String roleName;
}
