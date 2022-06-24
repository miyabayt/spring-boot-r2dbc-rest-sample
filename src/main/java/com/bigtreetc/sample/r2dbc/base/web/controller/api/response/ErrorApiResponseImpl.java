package com.bigtreetc.sample.r2dbc.base.web.controller.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorApiResponseImpl implements ErrorApiResponse {

  // リクエストID
  String requestId;

  // 入力エラー
  @JsonInclude(Include.NON_NULL)
  List<FieldErrorDto> fieldErrors;

  // メッセージ
  String message;

  // 成功フラグ
  boolean success = false;

  public ErrorApiResponseImpl() {}
}
