package com.bigtreetc.sample.r2dbc.controller.mailtemplates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true) // 定義されていないプロパティを無視してマッピングする
@JsonPropertyOrder({"メールテンプレートID", "メールテンプレートコード"}) // CSVのヘッダ順
@Getter
@Setter
public class MailTemplateCsv implements Serializable {

  private static final long serialVersionUID = -1L;

  @JsonProperty("メールテンプレートID")
  UUID id;

  @JsonProperty("メールテンプレートコード")
  String templateCode;

  @JsonProperty("メールタイトル")
  String subject;

  @JsonProperty("メール本文")
  String templateBody;
}
