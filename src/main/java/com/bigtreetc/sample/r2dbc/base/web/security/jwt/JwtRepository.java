package com.bigtreetc.sample.r2dbc.base.web.security.jwt;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEquals;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

@Getter
@Setter
@Slf4j
public class JwtRepository {

  private String signingKey;

  private long expiresIn;

  private ReactiveStringRedisTemplate redisTemplate;

  private int refreshTokenTimeoutHours;

  public Mono<String> createAccessToken(String username, List<String> authorities) {
    Date issuedAt = new Date();
    Date notBefore = new Date(issuedAt.getTime());
    Date expiresAt = new Date(issuedAt.getTime() + expiresIn);

    return Mono.just(
        JWT.create()
            .withIssuedAt(issuedAt)
            .withNotBefore(notBefore)
            .withExpiresAt(expiresAt)
            .withClaim(JwtConst.USERNAME, username)
            .withArrayClaim(JwtConst.ROLES, authorities.toArray(new String[0]))
            .sign(Algorithm.HMAC512(signingKey)));
  }

  public Mono<String> createRefreshToken(String username) {
    val refreshToken = RandomStringUtils.randomAlphanumeric(256);
    return storeRefreshToken(refreshToken, username).thenReturn(refreshToken);
  }

  public Mono<Boolean> verifyRefreshToken(String username, String refreshToken) {
    return redisTemplate
        .opsForValue()
        .get(refreshToken)
        .map(
            value -> {
              if (isNotEquals(username, value)) {
                log.warn("invalid refresh token. username={}", username);
                return false;
              }
              return true;
            });
  }

  public Mono<String> renewRefreshToken(String username, String refreshToken) {
    val newRefreshToken = RandomStringUtils.randomAlphanumeric(256);
    return redisTemplate
        .delete(refreshToken)
        .flatMap(count -> redisTemplate.opsForValue().set(newRefreshToken, username))
        .thenReturn(newRefreshToken);
  }

  public Mono<Boolean> deleteRefreshToken(String username, String refreshToken) {
    return redisTemplate.delete(refreshToken).map(deleted -> deleted == 1);
  }

  private Mono<Boolean> storeRefreshToken(String refreshToken, String username) {
    return redisTemplate
        .opsForValue()
        .set(refreshToken, username)
        .flatMap(
            done -> redisTemplate.expire(refreshToken, Duration.ofHours(refreshTokenTimeoutHours)))
        .doOnSuccess(
            done -> {
              if (log.isDebugEnabled()) {
                log.debug(
                    "refresh token has stored. [username={}, refreshToken={}]",
                    username,
                    refreshToken);
              }
              log.info("refresh token has stored. [username={}]", username);
            })
        .doOnError(e -> log.warn("failed to store refresh token.", e));
  }
}
