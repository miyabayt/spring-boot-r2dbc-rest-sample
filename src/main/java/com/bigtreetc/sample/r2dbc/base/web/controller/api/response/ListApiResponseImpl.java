package com.bigtreetc.sample.r2dbc.base.web.controller.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListApiResponseImpl implements ListApiResponse {

  long count;

  List<?> data;

  // 成功フラグ
  boolean success;

  // メッセージ
  String message;

  public ListApiResponseImpl(List<?> data, int count) {
    this.data = data;
    this.count = count;
  }

  @Override
  public void setCount(long count) {
    this.count = count;
  }

  @Override
  public boolean getSuccess() {
    return this.success;
  }

  @Override
  public void setSuccess(boolean success) {
    this.success = success;
  }

  @Override
  public ListApiResponse success(List<?> data) {
    this.setData(data);
    this.success();
    return this;
  }
}
