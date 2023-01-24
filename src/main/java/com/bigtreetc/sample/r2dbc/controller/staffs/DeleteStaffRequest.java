package com.bigtreetc.sample.r2dbc.controller.staffs;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteStaffRequest {

  private static final long serialVersionUID = -1L;

  UUID id;
}
