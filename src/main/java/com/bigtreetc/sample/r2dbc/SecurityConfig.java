package com.bigtreetc.sample.r2dbc;

import static com.bigtreetc.sample.r2dbc.base.web.BaseWebConst.*;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

import com.bigtreetc.sample.r2dbc.base.web.security.CorsProperties;
import com.bigtreetc.sample.r2dbc.base.web.security.JsonAccessDeniedHandler;
import com.bigtreetc.sample.r2dbc.base.web.security.jwt.*;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableConfigurationProperties({CorsProperties.class, JwtConfig.class})
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
public class SecurityConfig {

  @Autowired LettuceConnectionFactory lettuceConnectionFactory;

  @Autowired JwtConfig jwtConfig;

  @Bean
  public CorsWebFilter corsWebFilter(CorsProperties corsProperties) {
    val corsConfig = new CorsConfiguration();
    corsConfig.setAllowCredentials(corsProperties.getAllowCredentials());
    corsConfig.setAllowedHeaders(corsProperties.getAllowedHeaders());
    corsConfig.setAllowedMethods(corsProperties.getAllowedMethods());
    corsConfig.setAllowedOrigins(corsProperties.getAllowedOrigins());
    corsConfig.setExposedHeaders(corsProperties.getExposedHeaders());
    corsConfig.setMaxAge(corsProperties.getMaxAge());
    val source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return new CorsWebFilter(source);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ReactiveStringRedisTemplate redisTemplate() {
    return new ReactiveStringRedisTemplate(lettuceConnectionFactory);
  }

  @Bean
  public ReactiveAuthenticationManager authenticationManager(
      ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    val authenticationManager =
        new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    authenticationManager.setPasswordEncoder(passwordEncoder);
    return authenticationManager;
  }

  @Bean
  public JwtRepository jwtRepository(ReactiveStringRedisTemplate redisTemplate) {
    val accessTokenConfig = jwtConfig.getAccessToken();
    val signingKey = accessTokenConfig.getSigningKey();
    val expiresIn = accessTokenConfig.getExpiredIn();
    val refreshTokenConfig = jwtConfig.getRefreshToken();
    val timeoutHours = refreshTokenConfig.getTimeoutHours();
    val repository = new JwtRepository();
    repository.setRedisTemplate(redisTemplate);
    repository.setSigningKey(signingKey);
    repository.setExpiresIn(expiresIn);
    repository.setRefreshTokenTimeoutHours(timeoutHours);
    return repository;
  }

  @Bean
  public AuthenticationWebFilter authenticationWebFilter(
      ReactiveAuthenticationManager authenticationManager, JwtRepository jwtRepository) {
    val filter = new AuthenticationWebFilter(authenticationManager);
    filter.setRequiresAuthenticationMatcher(pathMatchers(HttpMethod.POST, API_AUTH_LOGIN_URL));
    filter.setServerAuthenticationConverter(new LoginRequestAuthenticationConverter());
    filter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler(jwtRepository));
    return filter;
  }

  @Bean
  public JwtRefreshFilter jwtRefreshFilter(JwtRepository jwtRepository) {
    val filter = new JwtRefreshFilter();
    filter.setRequiresAuthenticationMatcher(pathMatchers(HttpMethod.POST, API_AUTH_REFRESH_URL));
    filter.setRepository(jwtRepository);
    return filter;
  }

  @Bean
  public JwtVerificationFilter jwtVerificationFilter() {
    val signingKey = jwtConfig.getAccessToken().getSigningKey();
    val matcher =
        new NegatedServerWebExchangeMatcher(
            pathMatchers(
                ACTUATOR_URL,
                SWAGGER_RESOURCES_URL,
                SWAGGER_UI_URL,
                SWAGGER_DOCS_URL,
                WEBJARS_URL));
    val filter = new JwtVerificationFilter();
    filter.setRequiresAuthenticationMatcher(matcher);
    filter.setAuthenticationConverter(new JwtAuthenticationConverter(signingKey));
    return filter;
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(
      ServerHttpSecurity http,
      AuthenticationWebFilter authenticationWebFilter,
      JwtRefreshFilter jwtRefreshFilter,
      JwtVerificationFilter jwtVerificationFilter) {

    String[] permittedUrls = {
      API_AUTH_LOGIN_URL,
      API_AUTH_REFRESH_URL,
      API_AUTH_LOGOUT_URL,
      ACTUATOR_URL,
      SWAGGER_RESOURCES_URL,
      SWAGGER_UI_URL,
      SWAGGER_DOCS_URL,
      WEBJARS_URL
    };

    http.csrf()
        .disable()
        .authorizeExchange()
        .pathMatchers(permittedUrls)
        .permitAll()
        .anyExchange()
        .authenticated()
        .and()
        .addFilterBefore(jwtRefreshFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .addFilterBefore(jwtVerificationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
        .exceptionHandling()
        .accessDeniedHandler(new JsonAccessDeniedHandler());

    return http.build();
  }
}
