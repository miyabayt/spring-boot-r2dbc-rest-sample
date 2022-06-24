package com.bigtreetc.sample.r2dbc.base.web.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

  private final JWTVerifier verifier;

  public JwtAuthenticationConverter(String signingKey) {
    this.verifier = JWT.require(Algorithm.HMAC512(signingKey)).build();
  }

  @Override
  public Mono<Authentication> convert(ServerWebExchange exchange) {
    val request = exchange.getRequest();
    val authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (!StringUtils.startsWithIgnoreCase(authorization, JwtConst.TOKEN_PREFIX)) {
      return Mono.error(new InsufficientAuthenticationException("no authorization header"));
    }
    val accessToken = authorization.substring(7);

    try {
      val jwt = this.verifier.verify(accessToken);
      val username = jwt.getClaim(JwtConst.USERNAME).asString();
      val roles = jwt.getClaim(JwtConst.ROLES).asList(String.class);
      val authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();
      val authentication = new JwtAuthenticationToken(username, null, authorities);
      return Mono.just(authentication);
    } catch (JWTVerificationException e) {
      return Mono.error(new InsufficientAuthenticationException("invalid jwt token", e));
    }
  }
}
