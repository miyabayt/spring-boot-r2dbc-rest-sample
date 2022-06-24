package com.bigtreetc.sample.r2dbc.base.web.security.jwt;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Setter
@Slf4j
public class JwtVerificationFilter implements WebFilter {

  private ServerAuthenticationFailureHandler authenticationFailureHandler =
      new ServerAuthenticationEntryPointFailureHandler(
          new HttpStatusServerEntryPoint(HttpStatus.FORBIDDEN));

  private ServerWebExchangeMatcher requiresAuthenticationMatcher;

  private ServerAuthenticationConverter authenticationConverter;

  private ServerAuthenticationSuccessHandler authenticationSuccessHandler =
      new WebFilterChainServerAuthenticationSuccessHandler();

  private ServerSecurityContextRepository securityContextRepository =
      NoOpServerSecurityContextRepository.getInstance();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return this.requiresAuthenticationMatcher
        .matches(exchange)
        .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
        .flatMap((matchResult) -> this.authenticationConverter.convert(exchange))
        .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
        .flatMap((token) -> onAuthenticationSuccess(token, new WebFilterExchange(exchange, chain)))
        .onErrorResume(
            AuthenticationException.class,
            (ex) ->
                this.authenticationFailureHandler.onAuthenticationFailure(
                    new WebFilterExchange(exchange, chain), ex));
  }

  protected Mono<Void> onAuthenticationSuccess(
      Authentication authentication, WebFilterExchange webFilterExchange) {
    val exchange = webFilterExchange.getExchange();
    val securityContext = new SecurityContextImpl();
    securityContext.setAuthentication(authentication);
    return this.securityContextRepository
        .save(exchange, securityContext)
        .then(
            this.authenticationSuccessHandler.onAuthenticationSuccess(
                webFilterExchange, authentication))
        .contextWrite(
            ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
  }
}
