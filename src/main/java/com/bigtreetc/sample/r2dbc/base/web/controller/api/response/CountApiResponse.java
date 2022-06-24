package com.bigtreetc.sample.r2dbc.base.web.controller.api.response;

public interface CountApiResponse extends ApiResponse {

  Integer getCount();

  void setCount(Integer count);

  default CountApiResponse success(Integer count) {
    this.success();
    this.setCount(count);
    return this;
  }
}
