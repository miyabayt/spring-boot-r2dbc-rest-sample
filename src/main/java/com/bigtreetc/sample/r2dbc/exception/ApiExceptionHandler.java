package com.bigtreetc.sample.r2dbc.exception;

import com.bigtreetc.sample.r2dbc.base.web.controller.api.BaseApiExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler extends BaseApiExceptionHandler {

  // 独自の例外をハンドルする場合はここに定義する
}
