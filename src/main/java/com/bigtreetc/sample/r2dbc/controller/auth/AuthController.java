package com.bigtreetc.sample.r2dbc.controller.auth;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.repository.StaffRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "認証")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AuthController extends AbstractRestController {

  @NonNull final StaffRepository staffRepository;

  /**
   * ログインユーザを取得します。
   *
   * @return
   */
  @GetMapping("/me")
  public Mono<ApiResponse> getMe() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getName)
        .flatMap(staffId -> staffRepository.findById(UUID.fromString(staffId)))
        .switchIfEmpty(Mono.error(new NoDataFoundException("データが見つかりません。")))
        .flatMap(this::toApiResponse);
  }
}
