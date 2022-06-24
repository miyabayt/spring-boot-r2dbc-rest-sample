package com.bigtreetc.sample.r2dbc.base.web.controller.api.response;

import org.springframework.data.domain.Page;

public interface PageableApiResponse extends ListApiResponse {

  int getPage();

  int getTotalPages();

  void setPage(int page);

  void setTotalPages(int totalPages);

  default PageableApiResponse success(Page<?> page) {
    this.setPage(page.getNumber());
    this.setTotalPages(page.getTotalPages());
    this.setCount(page.getTotalElements());
    this.setData(page.getContent());
    this.success();
    return this;
  }
}
