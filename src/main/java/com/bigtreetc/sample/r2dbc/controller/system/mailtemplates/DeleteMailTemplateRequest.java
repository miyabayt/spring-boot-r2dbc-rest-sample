package com.bigtreetc.sample.r2dbc.controller.system.mailtemplates;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteMailTemplateRequest {

  private static final long serialVersionUID = -1L;

  UUID id;
}
