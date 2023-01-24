package com.bigtreetc.sample.r2dbc.controller.holidays;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchHolidayRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // 祝日名
  String holidayName;

  // 日付
  LocalDate holidayDate;
}
