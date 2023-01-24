package com.bigtreetc.sample.r2dbc.controller.users;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteUserRequest {

  private static final long serialVersionUID = -1L;

  UUID id;
}
