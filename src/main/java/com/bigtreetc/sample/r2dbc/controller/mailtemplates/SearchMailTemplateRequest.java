package com.bigtreetc.sample.r2dbc.controller.mailtemplates;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchMailTemplateRequest {

  private static final long serialVersionUID = -1L;

  UUID id;

  // メールテンプレートコード
  String templateCode;

  // メールタイトル
  String subject;

  // メール本文
  String templateBody;
}
