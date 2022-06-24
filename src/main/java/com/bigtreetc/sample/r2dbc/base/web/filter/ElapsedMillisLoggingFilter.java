package com.bigtreetc.sample.r2dbc.base.web.filter;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class ElapsedMillisLoggingFilter implements WebFilter {

  @Setter
  private ServerWebExchangeMatcher requiresAuthenticationMatcher =
      ServerWebExchangeMatchers.anyExchange();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    val beforeNanoSec = System.nanoTime();
    return this.requiresAuthenticationMatcher
        .matches(exchange)
        .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
        .flatMap(
            (matchResult) -> {
              val requestPath = exchange.getRequest().getPath().value();
              val requestMethod = exchange.getRequest().getMethodValue();
              val responseStatus = exchange.getResponse().getRawStatusCode();
              val elapsedNanoSec = System.nanoTime() - beforeNanoSec;
              val elapsedMilliSec = NANOSECONDS.toMillis(elapsedNanoSec);
              log.info(
                  "path={}, method={}, status={}, Elapsed={}ms.",
                  requestPath,
                  requestMethod,
                  responseStatus,
                  elapsedMilliSec);
              return Mono.empty();
            })
        .switchIfEmpty(chain.filter(exchange))
        .then();
  }
}
