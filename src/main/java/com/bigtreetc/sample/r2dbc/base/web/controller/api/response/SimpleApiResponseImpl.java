package com.bigtreetc.sample.r2dbc.base.web.controller.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleApiResponseImpl implements SimpleApiResponse {

  @JsonInclude(Include.NON_NULL)
  Object data;

  // 成功フラグ
  boolean success;

  // メッセージ
  String message;

  @Override
  public boolean getSuccess() {
    return this.success;
  }

  @Override
  public void setSuccess(boolean success) {
    this.success = success;
  }
}
