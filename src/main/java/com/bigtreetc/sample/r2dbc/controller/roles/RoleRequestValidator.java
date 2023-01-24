package com.bigtreetc.sample.r2dbc.controller.roles;

import com.bigtreetc.sample.r2dbc.base.domain.validator.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/** ロールマスタ登録 入力チェック */
@Component
public class RoleRequestValidator extends AbstractValidator<RoleRequest> {

  @Override
  protected void doValidate(RoleRequest request, Errors errors) {}
}
