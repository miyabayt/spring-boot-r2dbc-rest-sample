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
public class PageableApiResponseImpl implements PageableApiResponse {

  int page = 1;

  int perpage = 10;

  long count;

  int totalPages;

  List<?> data;

  // 成功フラグ
  boolean success;

  // メッセージ
  String message;

  public PageableApiResponseImpl(List<?> data, int page, int perpage, long count, int totalPages) {
    this.data = data;
    this.page = page;
    this.perpage = perpage;
    this.count = count;
    this.totalPages = totalPages;
  }

  @Override
  public boolean getSuccess() {
    return this.success;
  }
}
