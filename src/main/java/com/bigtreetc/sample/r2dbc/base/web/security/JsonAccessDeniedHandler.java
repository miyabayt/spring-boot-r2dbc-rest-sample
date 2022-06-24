package com.bigtreetc.sample.r2dbc.base.web.security;

import static com.bigtreetc.sample.r2dbc.base.web.BaseWebConst.ACCESS_DENIED_ERROR;

import com.bigtreetc.sample.r2dbc.base.util.JacksonUtils;
import com.bigtreetc.sample.r2dbc.base.util.MessageUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ErrorApiResponseImpl;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** JSON形式で認可エラーをレスポンスするためのハンドラー */
@Slf4j
public class JsonAccessDeniedHandler implements ServerAccessDeniedHandler {

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
    return Mono.defer(() -> Mono.just(exchange.getResponse()))
        .flatMap(
            (response) -> {
              response.setStatusCode(HttpStatus.FORBIDDEN);
              response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

              val resource = new ErrorApiResponseImpl();
              resource.setRequestId(exchange.getRequest().getId());
              resource.setMessage(MessageUtils.getMessage(ACCESS_DENIED_ERROR));

              String responseBody = JacksonUtils.writeValueAsString(resource);
              byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
              DataBufferFactory dataBufferFactory = response.bufferFactory();
              DataBuffer buffer = dataBufferFactory.wrap(bytes);
              return response
                  .writeWith(Mono.just(buffer))
                  .doOnError((error) -> DataBufferUtils.release(buffer));
            });
  }
}
