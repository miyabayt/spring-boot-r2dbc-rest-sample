package com.bigtreetc.sample.r2dbc.base.web.security.jwt;

import com.bigtreetc.sample.r2dbc.base.util.JacksonUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.SimpleApiResponseImpl;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

public class JwtAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

  private final JwtRepository jwtRepository;

  public JwtAuthenticationSuccessHandler(JwtRepository jwtRepository) {
    this.jwtRepository = jwtRepository;
  }

  @Override
  public Mono<Void> onAuthenticationSuccess(
      WebFilterExchange exchange, Authentication authentication) {
    val response = exchange.getExchange().getResponse();

    val username = authentication.getName();
    val authorities =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    response.getHeaders().addIfAbsent(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    response.setStatusCode(HttpStatus.OK);
    return response.writeWith(
        jwtRepository
            .createAccessToken(username, authorities)
            .zipWith(jwtRepository.createRefreshToken(username))
            .map(
                tuple2 -> {
                  val jwtToken = new JwtObject();
                  jwtToken.setAccessToken(tuple2.getT1());
                  jwtToken.setRefreshToken(tuple2.getT2());
                  return jwtToken;
                })
            .map(
                jwtToken -> {
                  val resource = new SimpleApiResponseImpl();
                  resource.success(jwtToken);
                  return resource;
                })
            .map(JacksonUtils::writeValueAsBytes)
            .map(bytes -> response.bufferFactory().wrap(bytes)));
  }
}
