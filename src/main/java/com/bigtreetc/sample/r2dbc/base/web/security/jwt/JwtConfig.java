package com.bigtreetc.sample.r2dbc.base.web.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtConfig {
  private AccessTokenConfig accessToken;
  private RefreshTokenConfig refreshToken;

  @Setter
  @Getter
  public static class AccessTokenConfig {
    private String signingKey;
    private int expiredIn = 60;
  }

  @Setter
  @Getter
  public static class RefreshTokenConfig {
    private int timeoutHours = 2;
  }
}
