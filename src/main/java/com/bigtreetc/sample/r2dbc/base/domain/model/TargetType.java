package com.bigtreetc.sample.r2dbc.base.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import java.util.stream.Stream;

/** 対象区分 */
public enum TargetType implements StringValue {
  非対象("0"),
  対象("1");

  private String value;

  TargetType(String value) {
    this.value = value;
  }

  public static TargetType of(final String value) {
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
