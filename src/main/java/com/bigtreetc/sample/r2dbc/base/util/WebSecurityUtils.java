package com.bigtreetc.sample.r2dbc.base.util;

import com.bigtreetc.sample.r2dbc.base.web.security.Role;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class WebSecurityUtils {

  private static final SpelParserConfiguration config = new SpelParserConfiguration(true, true);

  private static final SpelExpressionParser parser = new SpelExpressionParser(config);

  /**
   * 認証済みかどうかを示す値を返します。
   *
   * @return
   */
  public static boolean isAuthenticated() {
    val auth = SecurityContextHolder.getContext().getAuthentication();
    return auth.isAuthenticated();
  }

  /**
   * 引数に指定した権限を持っているかどうかを示す値を返します。
   *
   * @param a
   * @return
   */
  public static Mono<Boolean> hasAuthority(final String a) {
    return getAuthorities()
        .any(
            ga -> {
              val authority = ga.getAuthority();
              val expressionString = String.format("'%s' matches '%s'", a, authority);
              val expression = parser.parseExpression(expressionString);
              val isAllowed = expression.getValue(Boolean.class);
              log.debug("{} matches {}, result={}", a, authority, isAllowed);
              return Boolean.TRUE.equals(isAllowed);
            });
  }

  /**
   * 引数に指定したロールを持っているかどうかを示す値を返します。
   *
   * @param role
   * @return
   */
  public static Mono<Boolean> hasRole(final Role role) {
    return getAuthorities().any(r -> r.getAuthority().equals(role.name()));
  }

  /**
   * 引数に指定したロールを持っているかどうかを示す値を返します。
   *
   * @param role
   * @return
   */
  public static Mono<Boolean> hasRole(final String role) {
    return getAuthorities().any(r -> r.getAuthority().equals(role));
  }

  /**
   * 管理者のロールを持っているかどうかを示す値を返します。
   *
   * @return
   */
  public static Mono<Boolean> hasAdminRole() {
    return WebSecurityUtils.hasRole(Role.ROLE_ADMIN);
  }

  /**
   * ユーザのロールを持っているかどうかを示す値を返します。
   *
   * @return
   */
  public static Mono<Boolean> hasUserRole() {
    return WebSecurityUtils.hasRole(Role.ROLE_USER);
  }

  /**
   * 認証に使用されるIDを返します。
   *
   * @return
   */
  public static Optional<String> getPrincipal() {
    val authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      val principal = authentication.getPrincipal();

      if (principal instanceof String) {
        return Optional.of((String) principal);
      }
    }

    return Optional.empty();
  }

  /**
   * 保有している権限を返します。
   *
   * @return
   */
  public static Flux<? extends GrantedAuthority> getAuthorities() {
    return ReactiveSecurityContextHolder.getContext()
        .flatMapIterable(context -> context.getAuthentication().getAuthorities());
  }
}
