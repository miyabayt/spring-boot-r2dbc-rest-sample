package com.bigtreetc.sample.r2dbc.base.web.controller.api;

import static com.bigtreetc.sample.r2dbc.base.web.BaseWebConst.*;

import com.bigtreetc.sample.r2dbc.base.exception.DataInUseErrorException;
import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.base.exception.ValidationErrorException;
import com.bigtreetc.sample.r2dbc.base.util.MessageUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ErrorApiResponseImpl;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.FieldErrorDto;
import jakarta.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** API用の例外ハンドラー */
@Slf4j
public class BaseApiExceptionHandler {

  /**
   * 入力チェックエラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<?>> handleException(
      ConstraintViolationException ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(VALIDATION_ERROR, locale);

    var fieldErrors =
        ex.getConstraintViolations().stream()
            .map(
                cv -> {
                  val fieldName = cv.getPropertyPath().toString();
                  val rejectedValue = cv.getInvalidValue();
                  val errorMessage = cv.getMessage();
                  val fieldErrorResource = new FieldErrorDto();
                  fieldErrorResource.setFieldName(fieldName);
                  fieldErrorResource.setRejectedValue(rejectedValue);
                  fieldErrorResource.setErrorMessage(errorMessage);
                  return fieldErrorResource;
                })
            .toList();

    val resource = new ErrorApiResponseImpl();
    resource.setRequestId(exchange.getRequest().getId());
    resource.setFieldErrors(fieldErrors);
    resource.setMessage(message);

    return Mono.just(ResponseEntity.badRequest().body(resource));
  }

  /**
   * 入力チェックエラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<?>> handleException(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(VALIDATION_ERROR, locale);

    var fieldErrors =
        ex.getFieldErrors().stream()
            .map(
                fe -> {
                  val fieldName = fe.getField();
                  val rejectedValue = fe.getRejectedValue();
                  val errorMessage = fe.getDefaultMessage();
                  val fieldErrorResource = new FieldErrorDto();
                  fieldErrorResource.setFieldName(fieldName);
                  fieldErrorResource.setRejectedValue(rejectedValue);
                  fieldErrorResource.setErrorMessage(errorMessage);
                  return fieldErrorResource;
                })
            .toList();

    val resource = new ErrorApiResponseImpl();
    resource.setRequestId(exchange.getRequest().getId());
    resource.setFieldErrors(fieldErrors);
    resource.setMessage(message);

    return Mono.just(ResponseEntity.badRequest().body(resource));
  }

  /**
   * 入力チェックエラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(ValidationErrorException.class)
  public Mono<ResponseEntity<?>> handleValidationErrorException(
      ValidationErrorException ex, ServerWebExchange exchange) {
    val fieldErrorContexts = new ArrayList<FieldErrorDto>();

    ex.getErrors()
        .ifPresent(
            errors ->
                errors
                    .getFieldErrors()
                    .forEach(
                        fieldError -> {
                          val fieldName = fieldError.getField();
                          val rejectedValue = fieldError.getRejectedValue();
                          val errorMessage = MessageUtils.getMessage(fieldError);
                          val fieldErrorResource = new FieldErrorDto();
                          fieldErrorResource.setFieldName(fieldName);
                          fieldErrorResource.setRejectedValue(rejectedValue);
                          fieldErrorResource.setErrorMessage(errorMessage);
                          fieldErrorContexts.add(fieldErrorResource);
                        }));

    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(VALIDATION_ERROR, locale);

    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);
    errorResource.setFieldErrors(fieldErrorContexts);

    return Mono.just(ResponseEntity.badRequest().body(errorResource));
  }

  /**
   * データ不存在エラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(NoDataFoundException.class)
  public Mono<ResponseEntity<?>> handleNoDataFoundException(
      Exception ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(NO_DATA_FOUND_ERROR, locale);

    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);

    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource));
  }

  /**
   * ファイル不存在エラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(FileNotFoundException.class)
  public Mono<ResponseEntity<?>> handleFileNotFoundException(
      FileNotFoundException ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(FILE_NOT_FOUND_ERROR, locale);

    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);

    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResource));
  }

  /**
   * 使用中エラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(DataInUseErrorException.class)
  public Mono<ResponseEntity<?>> handleInUseErrorException(
      DataInUseErrorException ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(DATA_IN_USE_ERROR, locale);

    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);

    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResource));
  }

  /**
   * 認証エラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(AuthenticationException.class)
  public Mono<ResponseEntity<?>> handleAuthenticationException(
      AuthenticationException ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(UNAUTHORIZED_ERROR, locale);

    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);

    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResource));
  }

  /**
   * 不正アクセスエラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(AccessDeniedException.class)
  public Mono<ResponseEntity<?>> handleAccessDeniedException(
      AccessDeniedException ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(ACCESS_DENIED_ERROR, locale);

    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);

    return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResource));
  }

  /**
   * 重複エラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(DuplicateKeyException.class)
  public Mono<ResponseEntity<?>> handleDuplicateKeyException(
      DuplicateKeyException ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(DUPLICATE_KEY_ERROR, locale);

    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);

    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResource));
  }

  /**
   * 楽観的排他制御エラーのハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler({OptimisticLockingFailureException.class})
  public Mono<ResponseEntity<?>> handleOptimisticLockingFailureException(
      OptimisticLockingFailureException ex, ServerWebExchange exchange) {
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(OPTIMISTIC_LOCKING_FAILURE_ERROR, locale);
    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);

    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResource));
  }

  /**
   * 予期せぬ例外のハンドリング
   *
   * @param ex
   * @param exchange
   * @return
   */
  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<?>> handleUnexpectedException(
      Exception ex, ServerWebExchange exchange) {
    log.error("unexpected error", ex);
    val locale = exchange.getLocaleContext().getLocale();
    val message = MessageUtils.getMessage(UNEXPECTED_ERROR, locale);

    val errorResource = new ErrorApiResponseImpl();
    errorResource.setRequestId(exchange.getRequest().getId());
    errorResource.setMessage(message);

    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResource));
  }
}
