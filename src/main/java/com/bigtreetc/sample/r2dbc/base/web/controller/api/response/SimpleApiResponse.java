package com.bigtreetc.sample.r2dbc.base.web.controller.api.response;

public interface SimpleApiResponse extends ApiResponse {

  Object getData();

  void setData(Object data);

  default SimpleApiResponse success(Object data) {
    this.success();
    this.setData(data);
    return this;
  }
}
