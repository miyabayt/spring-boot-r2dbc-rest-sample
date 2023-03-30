package com.bigtreetc.sample.r2dbc.controller.holidays;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true) // 定義されていないプロパティを無視してマッピングする
@JsonPropertyOrder({"祝日ID", "名称", "日付"}) // CSVのヘッダ順
@Getter
@Setter
public class HolidayCsv implements Serializable {

  private static final long serialVersionUID = -1L;

  @JsonProperty("祝日ID")
  UUID id;

  @JsonProperty("名称")
  String holidayName;

  @JsonProperty("日付")
  @JsonFormat(pattern = "yyyy/MM/dd")
  LocalDate holidayDate;
}
