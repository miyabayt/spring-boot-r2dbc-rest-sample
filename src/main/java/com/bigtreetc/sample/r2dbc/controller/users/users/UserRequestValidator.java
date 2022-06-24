package com.bigtreetc.sample.r2dbc.controller.users.users;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEquals;

import com.bigtreetc.sample.r2dbc.base.domain.validator.AbstractValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/** ユーザ登録 入力チェック */
@Component
public class UserRequestValidator extends AbstractValidator<UserRequest> {

  @Override
  protected void doValidate(UserRequest request, Errors errors) {

    // 確認用パスワードと突き合わせる
    if (isNotEquals(request.getPassword(), request.getPasswordConfirm())) {
      errors.rejectValue("password", "users.unmatchPassword");
      errors.rejectValue("passwordConfirm", "users.unmatchPassword");
    }
  }
}
