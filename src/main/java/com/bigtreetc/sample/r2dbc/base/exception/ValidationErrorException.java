package com.bigtreetc.sample.r2dbc.base.exception;

import static java.util.Optional.ofNullable;

import java.util.Optional;
import org.springframework.validation.Errors;

/** バリデーションエラー */
public class ValidationErrorException extends RuntimeException {

  private static final long serialVersionUID = -1L;

  private Optional<Errors> errors;

  public ValidationErrorException(Errors errors) {
    super();
    this.errors = ofNullable(errors);
  }

  public Optional<Errors> getErrors() {
    return this.errors;
  }
}
