package com.bigtreetc.sample.r2dbc.base.web.security.jwt;

import java.util.Collections;
import lombok.val;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class LoginRequestAuthenticationConverter implements ServerAuthenticationConverter {

  private static final Jackson2JsonDecoder decoder = new Jackson2JsonDecoder();

  @Override
  public Mono<Authentication> convert(ServerWebExchange exchange) {
    val request = exchange.getRequest();
    val resolveType = ResolvableType.forClass(LoginRequest.class);

    return decoder
        .decodeToMono(
            request.getBody(), resolveType, MediaType.APPLICATION_JSON, Collections.emptyMap())
        .cast(LoginRequest.class)
        .map(
            loginRequest -> {
              val username = loginRequest.getUsername();
              val password = loginRequest.getPassword();
              return new UsernamePasswordAuthenticationToken(username, password);
            });
  }
}
