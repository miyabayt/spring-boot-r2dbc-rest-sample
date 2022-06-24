package com.bigtreetc.sample.r2dbc.controller.system.codes;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteCodeRequest {

  private static final long serialVersionUID = -1L;

  UUID id;
}
