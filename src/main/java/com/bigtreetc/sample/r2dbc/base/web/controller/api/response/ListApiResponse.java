package com.bigtreetc.sample.r2dbc.base.web.controller.api.response;

import java.util.List;

public interface ListApiResponse extends ApiResponse {

  List<?> getData();

  void setData(List<?> data);

  long getCount();

  void setCount(long count);

  String getMessage();

  void setMessage(String message);

  default ListApiResponse success(List<?> data) {
    this.setData(data);
    this.success();
    return this;
  }

  default ListApiResponse success(List<?> data, long count) {
    this.setData(data);
    this.setCount(count);
    this.success();
    return this;
  }
}
