package com.bigtreetc.sample.r2dbc.base.exception;

/** データ不存在エラー */
public class NoDataFoundException extends RuntimeException {

  private static final long serialVersionUID = -1L;

  /** コンストラクタ */
  public NoDataFoundException(String message) {
    super(message);
  }

  /** コンストラクタ */
  public NoDataFoundException(Exception e) {
    super(e);
  }
}
