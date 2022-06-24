package com.bigtreetc.sample.r2dbc.base.web.controller.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldErrorDto {

  // 項目名
  String fieldName;

  // 入力値
  Object rejectedValue;

  // エラーメッセージ
  String errorMessage;
}
