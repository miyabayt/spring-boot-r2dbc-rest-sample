package com.bigtreetc.sample.r2dbc.controller.system.codecategories;

import com.bigtreetc.sample.r2dbc.base.domain.validator.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/** コード分類マスタ登録 入力チェック */
@Component
public class CodeCategoryRequestValidator extends AbstractValidator<CodeCategoryRequest> {

  @Override
  protected void doValidate(CodeCategoryRequest request, Errors errors) {}
}
