package com.bigtreetc.sample.r2dbc.controller.system.staffs;

import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchStaffRequest implements Serializable {

  private static final long serialVersionUID = -1L;

  UUID id;

  String firstName;

  String lastName;

  String email;
}
