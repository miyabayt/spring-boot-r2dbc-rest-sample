package com.bigtreetc.sample.r2dbc.base.web.security.jwt;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {

  String username;

  String password;
}
