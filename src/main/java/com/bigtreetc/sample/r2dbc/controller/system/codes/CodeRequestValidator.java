package com.bigtreetc.sample.r2dbc.controller.system.codes;

import com.bigtreetc.sample.r2dbc.base.domain.validator.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/** コード登録 入力チェック */
@Component
public class CodeRequestValidator extends AbstractValidator<CodeRequest> {

  @Override
  protected void doValidate(CodeRequest request, Errors errors) {}
}
