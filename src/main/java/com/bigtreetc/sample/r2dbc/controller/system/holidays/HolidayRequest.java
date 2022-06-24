package com.bigtreetc.sample.r2dbc.controller.system.holidays;

import java.time.LocalDate;
import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HolidayRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // 祝日名
  @NotEmpty String holidayName;

  // 日付
  LocalDate holidayDate;

  // 改定番号
  Integer version;
}
