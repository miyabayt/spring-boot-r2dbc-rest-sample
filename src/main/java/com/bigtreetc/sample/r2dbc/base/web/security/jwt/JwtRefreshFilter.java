package com.bigtreetc.sample.r2dbc.base.web.security.jwt;

import static com.bigtreetc.sample.r2dbc.base.web.BaseWebConst.UNAUTHORIZED_ERROR;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bigtreetc.sample.r2dbc.base.util.JacksonUtils;
import com.bigtreetc.sample.r2dbc.base.util.MessageUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ErrorApiResponseImpl;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.SimpleApiResponseImpl;
import java.util.Collections;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Setter
@Slf4j
public class JwtRefreshFilter implements WebFilter {

  private static final Jackson2JsonDecoder decoder = new Jackson2JsonDecoder();

  private JwtRepository repository;

  private ServerWebExchangeMatcher requiresAuthenticationMatcher;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    val response = exchange.getResponse();

    return this.requiresAuthenticationMatcher
        .matches(exchange)
        .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
        .flatMap(
            matchResult -> {
              val request = exchange.getRequest();
              val resolveType = ResolvableType.forClass(RefreshTokenRequest.class);
              return decoder
                  .decodeToMono(
                      request.getBody(),
                      resolveType,
                      MediaType.APPLICATION_JSON,
                      Collections.emptyMap())
                  .cast(RefreshTokenRequest.class);
            })
        .flatMap(
            refreshTokenRequest -> {
              response
                  .getHeaders()
                  .addIfAbsent(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
              response.setStatusCode(HttpStatus.OK);
              return response.writeWith(
                  getJwtToken(refreshTokenRequest)
                      .map(
                          jwtToken -> {
                            val resource = new SimpleApiResponseImpl();
                            resource.success(jwtToken);
                            return resource;
                          })
                      .map(JacksonUtils::writeValueAsBytes)
                      .map(bytes -> response.bufferFactory().wrap(bytes)));
            })
        .onErrorResume(
            Exception.class,
            e -> {
              log.debug("cloud not refresh token.", e);
              response
                  .getHeaders()
                  .addIfAbsent(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
              response.setStatusCode(HttpStatus.UNAUTHORIZED);
              return response.writeWith(
                  Mono.fromCallable(
                          () -> {
                            val message = MessageUtils.getMessage(UNAUTHORIZED_ERROR);
                            val resource = new ErrorApiResponseImpl();
                            resource.setRequestId(exchange.getRequest().getId());
                            resource.setMessage(e.getMessage());
                            return resource;
                          })
                      .map(JacksonUtils::writeValueAsBytes)
                      .map(bytes -> response.bufferFactory().wrap(bytes)));
            })
        .switchIfEmpty(chain.filter(exchange));
  }

  private Mono<JwtObject> getJwtToken(RefreshTokenRequest request) {
    return Mono.defer(
        () -> {
          val accessToken = request.getAccessToken();
          val refreshToken = request.getRefreshToken();
          val jwt = parseToken(accessToken);
          val username = getUsername(jwt);
          val authorities = getAuthorities(jwt);

          return repository
              .verifyRefreshToken(username, refreshToken)
              .flatMap(
                  valid -> {
                    if (!valid) {
                      return Mono.error(
                          new InsufficientAuthenticationException("リフレッシュトークンが不正です。"));
                    }
                    return Mono.just(true);
                  })
              .flatMap(
                  valid ->
                      repository
                          .createAccessToken(username, authorities)
                          .zipWith(repository.renewRefreshToken(username, refreshToken)))
              .flatMap(
                  tuple2 -> {
                    val newAccessToken = tuple2.getT1();
                    val newRefreshToken = tuple2.getT2();
                    val jwtToken = new JwtObject();
                    jwtToken.setAccessToken(newAccessToken);
                    jwtToken.setRefreshToken(newRefreshToken);

                    return Mono.just(jwtToken);
                  });
        });
  }

  private DecodedJWT parseToken(String accessToken) {
    DecodedJWT jwt = null;
    try {
      jwt = JWT.decode(accessToken);
    } catch (Exception e) {
      throw new JWTDecodeException("アクセストークンが不正です。");
    }
    return jwt;
  }

  private String getUsername(DecodedJWT jwt) {
    val username = jwt.getClaim(JwtConst.USERNAME).asString();
    if (username == null || "".equals(username)) {
      String msg = String.format("username=%s", username);
      throw new JWTVerificationException(msg);
    }
    return username;
  }

  private List<String> getAuthorities(DecodedJWT jwt) {
    return jwt.getClaim(JwtConst.ROLES).asList(String.class);
  }
}
