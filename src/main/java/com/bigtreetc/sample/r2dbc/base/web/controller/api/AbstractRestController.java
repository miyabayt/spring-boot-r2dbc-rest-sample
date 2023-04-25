package com.bigtreetc.sample.r2dbc.base.web.controller.api;

import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractRestController {

  @Autowired protected ModelMapper modelMapper;

  protected Mono<ApiResponse> toApiResponse(int count) {
    return Mono.just(new CountApiResponseImpl().success(count));
  }

  protected Mono<ApiResponse> toApiResponse(List<?> data) {
    return Mono.just(new ListApiResponseImpl().success(data));
  }

  protected Mono<ApiResponse> toApiResponse(Page<?> data) {
    return Mono.just(new PageableApiResponseImpl().success(data));
  }

  protected Mono<ApiResponse> toApiResponse(Object data) {
    return Mono.just(new SimpleApiResponseImpl().success(data));
  }

  protected ApiResponse createSimpleApiResponse() {
    return new SimpleApiResponseImpl().success();
  }

  @SneakyThrows
  protected ResponseEntity<Resource> toResponseEntity(Resource resource, String filename) {
    return toResponseEntity(resource, filename, false);
  }

  @SneakyThrows
  protected ResponseEntity<Resource> toResponseEntity(
      Resource resource, String filename, boolean isAttachment) {

    val responseEntity =
        ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

    if (isAttachment) {
      val encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
      val contentDisposition = String.format("attachment; filename*=UTF-8''%s", encodedFilename);
      responseEntity.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
    }

    return responseEntity.body(resource);
  }

  @SneakyThrows
  protected void setContentDispositionHeader(
      ServerHttpResponse response, String filename, boolean isAttachment) {
    response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

    if (isAttachment) {
      val encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
      val contentDisposition = String.format("attachment; filename*=UTF-8''%s", encodedFilename);
      response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
    }
  }
}
