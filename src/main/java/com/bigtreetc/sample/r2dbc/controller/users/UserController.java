package com.bigtreetc.sample.r2dbc.controller.users;

import static com.bigtreetc.sample.r2dbc.base.util.TypeUtils.toListType;
import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;

import com.bigtreetc.sample.r2dbc.base.util.CsvUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.request.Requests;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.model.User;
import com.bigtreetc.sample.r2dbc.domain.model.UserCriteria;
import com.bigtreetc.sample.r2dbc.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "ユーザ")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UserController extends AbstractRestController {

  @NonNull final UserRequestValidator userRequestValidator;

  @NonNull final UserService userService;

  @NonNull final PasswordEncoder passwordEncoder;

  @InitBinder
  public void validatorBinder(WebDataBinder binder) {
    binder.addValidators(userRequestValidator);
  }

  /**
   * ユーザを登録します。
   *
   * @param requestMono
   * @return
   */
  @Operation(summary = "ユーザ登録", description = "ユーザを登録します。")
  @PreAuthorize("hasAuthority('user:save')")
  @PostMapping("/user")
  public Mono<ApiResponse> create(@Validated @RequestBody Mono<UserRequest> requestMono) {
    return requestMono
        .map(
            request -> {
              val inputUser = modelMapper.map(request, User.class);
              val password = request.getPassword();

              // パスワードをハッシュ化する
              inputUser.setPassword(passwordEncoder.encode(password));
              return inputUser;
            })
        .flatMap(userService::create)
        .flatMap(this::toApiResponse);
  }

  /**
   * ユーザを一括登録します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "ユーザ一括登録", description = "ユーザを一括登録します。")
  @PreAuthorize("hasAuthority('user:save')")
  @PostMapping(value = "/users")
  public Mono<ApiResponse> createAll(
      @Validated @RequestBody Mono<Requests<UserRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, User.class)).toList())
        .flatMap(users -> userService.create(users).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * ユーザを検索します。
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "ユーザ検索", description = "ユーザを検索します。")
  @PreAuthorize("hasAuthority('user:read')")
  @GetMapping("/users")
  public Mono<ApiResponse> index(
      @ModelAttribute SearchUserRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, UserCriteria.class);
    return userService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * ユーザを取得します。
   *
   * @param userId
   * @return
   */
  @Operation(summary = "ユーザ取得", description = "ユーザを取得します。")
  @PreAuthorize("hasAuthority('user:read')")
  @GetMapping("/user/{userId}")
  public Mono<ApiResponse> show(@PathVariable UUID userId) {
    return userService.findById(userId).flatMap(this::toApiResponse);
  }

  /**
   * ユーザを更新します。
   *
   * @param userId
   * @param requestMono
   * @return
   */
  @Operation(summary = "ユーザ更新", description = "ユーザを更新します。")
  @PreAuthorize("hasAuthority('user:save')")
  @PutMapping("/user/{userId}")
  public Mono<ApiResponse> update(
      @PathVariable UUID userId, @Validated @RequestBody Mono<UserRequest> requestMono) {
    return userService
        .findById(userId)
        .zipWith(requestMono)
        .map(
            tuple2 -> {
              val user = tuple2.getT1();
              val request = tuple2.getT2();
              modelMapper.map(request, user);
              val password = user.getPassword();
              if (isNotEmpty(password)) {
                user.setPassword(passwordEncoder.encode(password));
              }
              return user;
            })
        .flatMap(userService::update)
        .flatMap(this::toApiResponse);
  }

  /**
   * ユーザを一括更新します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "ユーザ一括更新", description = "ユーザを一括更新します。")
  @PreAuthorize("hasAuthority('user:save')")
  @PutMapping(value = "/users")
  public Mono<ApiResponse> update(
      @Validated @RequestBody Mono<Requests<UserRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, User.class)).toList())
        .flatMap(users -> userService.update(users).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * ユーザを削除します。
   *
   * @param userId
   * @return
   */
  @Operation(summary = "ユーザ削除", description = "ユーザを削除します。")
  @PreAuthorize("hasAuthority('user:save')")
  @DeleteMapping("/user/{userId}")
  public Mono<ApiResponse> delete(@PathVariable UUID userId) {
    return userService
        .findById(userId)
        .flatMap(user -> userService.delete(userId).thenReturn(user))
        .flatMap(this::toApiResponse);
  }

  /**
   * ユーザを一括削除します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "ユーザ一括削除", description = "ユーザを一括削除します。")
  @PreAuthorize("hasAuthority('user:save')")
  @DeleteMapping(value = "/users")
  public Mono<ApiResponse> delete(
      @Validated @RequestBody Mono<Requests<DeleteUserRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(DeleteUserRequest::getId).toList())
        .flatMap(userService::delete)
        .thenReturn(createSimpleApiResponse());
  }

  /**
   * CSV出力
   *
   * @param filename
   * @return
   */
  @Operation(summary = "ユーザCSV出力", description = "CSVファイルを出力します。")
  @PreAuthorize("hasAuthority('user:read')")
  @GetMapping("/users/export/{filename:.+\\.csv}")
  public Mono<ResponseEntity<Resource>> downloadCsv(
      @PathVariable String filename, ServerHttpResponse response) {
    return userService
        .findAll(new UserCriteria(), Pageable.unpaged())
        .map(
            pages -> {
              val csvList = modelMapper.map(pages.getContent(), toListType(UserCsv.class));
              val dataBuffer = response.bufferFactory().allocateBuffer();
              CsvUtils.writeCsv(UserCsv.class, csvList, dataBuffer);
              return new InputStreamResource(dataBuffer.asInputStream(true));
            })
        .map(resource -> toResponseEntity(resource, filename, true));
  }
}
