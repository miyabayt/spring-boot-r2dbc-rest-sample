package com.bigtreetc.sample.r2dbc.controller.staffs;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StaffRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // 名
  @NotEmpty String firstName;

  // 姓
  @NotEmpty String lastName;

  @NotEmpty String password;

  @NotEmpty String passwordConfirm;

  // メールアドレス
  @NotEmpty @Email String email;

  // 電話番号
  @Digits(fraction = 0, integer = 10)
  String tel;

  // 改定番号
  Integer version;
}
