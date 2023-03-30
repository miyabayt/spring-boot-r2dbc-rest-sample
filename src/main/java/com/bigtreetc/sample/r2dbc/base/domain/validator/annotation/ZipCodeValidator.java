package com.bigtreetc.sample.r2dbc.base.domain.validator.annotation;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isEmpty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 入力チェック（郵便番号） */
public class ZipCodeValidator implements ConstraintValidator<ZipCode, String> {

  static final Pattern p = Pattern.compile("^[0-9]{7}$");

  @Override
  public void initialize(ZipCode ZipCode) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    boolean isValid = false;

    if (isEmpty(value)) {
      isValid = true;
    } else {
      Matcher m = p.matcher(value);

      if (m.matches()) {
        isValid = true;
      }
    }

    return isValid;
  }
}
