package com.bigtreetc.sample.r2dbc.controller.system.mailtemplates;

import com.bigtreetc.sample.r2dbc.base.domain.validator.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/** メールテンプレート登録 入力チェック */
@Component
public class MailTemplateRequestValidator extends AbstractValidator<MailTemplateRequest> {

  @Override
  protected void doValidate(MailTemplateRequest request, Errors errors) {}
}
