package com.bigtreetc.sample.r2dbc.base.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import java.util.stream.Stream;

/** 表示ステータス */
public enum DisplayStatus implements StringValue {
  非表示("0"),
  表示("1");

  private String value;

  DisplayStatus(String value) {
    this.value = value;
  }

  public static DisplayStatus of(final String value) {
    return Stream.of(values())
        .filter(v -> Objects.equals(v.value, value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("不正な引数が指定されました。[" + value + "]"));
  }

  public String getName() {
    return name();
  }

  @JsonValue
  public String getValue() {
    return this.value;
  }
}
