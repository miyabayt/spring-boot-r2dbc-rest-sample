package com.bigtreetc.sample.r2dbc.controller.system.holidays;

import com.bigtreetc.sample.r2dbc.base.domain.validator.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/** 祝日登録 入力チェック */
@Component
public class HolidayRequestValidator extends AbstractValidator<HolidayRequest> {

  @Override
  protected void doValidate(HolidayRequest request, Errors errors) {}
}
