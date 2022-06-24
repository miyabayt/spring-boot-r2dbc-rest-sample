package com.bigtreetc.sample.r2dbc.base.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/** 入力チェックユーティリティ */
public class ValidateUtils {

  /**
   * 引数の値の真偽を示す値を返します。
   *
   * @param value
   * @return
   */
  public static boolean isTrue(Boolean value) {
    return value != null && value;
  }

  /**
   * 値の真偽を示す値を返します。
   *
   * @param value
   * @return
   */
  public static boolean isFalse(Boolean value) {
    return !isTrue(value);
  }

  /**
   * 値の存在可否をチェックします。
   *
   * @param value
   * @return
   */
  public static boolean isEmpty(String value) {
    return value == null || value.length() == 0;
  }

  /**
   * コレクションの存在可否をチェックします。
   *
   * @param collection
   * @return
   */
  public static boolean isEmpty(Collection<?> collection) {
    return (collection == null || collection.isEmpty());
  }

  /**
   * 配列の存在可否をチェックします。
   *
   * @param array
   * @return
   */
  public static boolean isEmpty(Object[] array) {
    return (array == null || array.length == 0);
  }

  /**
   * マップの存在可否をチェックします。
   *
   * @param map
   * @return
   */
  public static boolean isEmpty(Map<?, ?> map) {
    return (map == null || map.isEmpty());
  }

  /**
   * 存在可否をチェックします。
   *
   * @param value
   * @return
   */
  public static boolean isNotEmpty(String value) {
    return !isEmpty(value);
  }

  /**
   * 存在可否をチェックします。
   *
   * @param collection
   * @return
   */
  public static boolean isNotEmpty(Collection<?> collection) {
    return !isEmpty(collection);
  }

  /**
   * 存在可否をチェックします。
   *
   * @param array
   * @return
   */
  public static boolean isNotEmpty(Object[] array) {
    return !isEmpty(array);
  }

  /**
   * 存在可否をチェックします。
   *
   * @param map
   * @return
   */
  public static boolean isNotEmpty(Map<?, ?> map) {
    return !isEmpty(map);
  }

  /**
   * 文字列と正規表現のマッチ可否をチェックします。
   *
   * @param value
   * @param regex
   * @return
   */
  public static boolean matches(String value, String regex) {
    return isNotEmpty(value) && value.matches(regex);
  }

  /**
   * 値が数字のみかチェックします。
   *
   * @param value
   * @return
   */
  public static boolean isNumeric(String value) {
    if (isEmpty(value)) {
      return false;
    }
    for (int i = 0; i < value.length(); i++) {
      if (!Character.isDigit(value.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * 値がASCII文字のみかチェックします。
   *
   * @param value
   * @return
   */
  public static boolean isAscii(String value) {
    if (isEmpty(value)) {
      return false;
    }
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (!(ch < 128)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 値がASCII文字のみかチェックします。
   *
   * @param value
   * @return
   */
  public static boolean isAsciiPrintable(String value) {
    if (isEmpty(value)) {
      return false;
    }
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      if (!(ch >= 32 && ch < 127)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 引数同士が等しいかチェックします。
   *
   * @param obj1
   * @param obj2
   * @return
   */
  public static boolean isEquals(Object obj1, Object obj2) {
    return Objects.equals(obj1, obj2);
  }

  /**
   * 引数同士が等しいかチェックします。
   *
   * @param obj1
   * @param obj2
   * @return
   */
  public static boolean isNotEquals(Object obj1, Object obj2) {
    return !isEquals(obj1, obj2);
  }
}
