package com.bigtreetc.sample.r2dbc.controller.codecategories;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchCodeCategoryRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  String categoryCode;

  String categoryName;
}
