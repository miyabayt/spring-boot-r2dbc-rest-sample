package com.bigtreetc.sample.r2dbc.base.domain.model;

import java.util.Objects;
import java.util.stream.Stream;

/** 性別 */
public enum Genders implements StringValue {
  男性("1"),
  女性("2"),
  不明("9");

  private String value;

  Genders(String value) {
    this.value = value;
  }

  public static Genders of(final String value) {
    return Stream.of(values())
        .filter(v -> Objects.equals(v.value, value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("不正な引数が指定されました。[" + value + "]"));
  }

  public String getName() {
    return name();
  }

  public String getValue() {
    return this.value;
  }
}
