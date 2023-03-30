package com.bigtreetc.sample.r2dbc.controller.roles;

import static com.bigtreetc.sample.r2dbc.base.util.TypeUtils.toListType;

import com.bigtreetc.sample.r2dbc.base.util.CsvUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.request.Requests;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.model.Role;
import com.bigtreetc.sample.r2dbc.domain.model.RoleCriteria;
import com.bigtreetc.sample.r2dbc.domain.model.RolePermission;
import com.bigtreetc.sample.r2dbc.domain.service.PermissionService;
import com.bigtreetc.sample.r2dbc.domain.service.RoleService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "ロールマスタ")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/system", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class RoleController extends AbstractRestController {

  @NonNull final RoleRequestValidator roleRequestValidator;

  @NonNull final RoleService roleService;

  @NonNull final PermissionService permissionService;

  @InitBinder
  public void validatorBinder(WebDataBinder binder) {
    binder.addValidators(roleRequestValidator);
  }

  /**
   * ロールマスタを登録します。
   *
   * @param requestMono
   * @return
   */
  @Operation(summary = "ロールマスタ登録", description = "ロールマスタを登録します。")
  @PreAuthorize("hasAuthority('role:save')")
  @PostMapping("/role")
  public Mono<ApiResponse> create(@Validated @RequestBody Mono<RoleRequest> requestMono) {
    return requestMono
        .map(
            request -> {
              val inputRole = modelMapper.map(request, Role.class);
              request
                  .getPermissions()
                  .forEach(
                      (permissionCode, isEnabled) -> {
                        val rp = new RolePermission();
                        rp.setId(UUID.randomUUID());
                        rp.setRoleCode(request.getRoleCode());
                        rp.setPermissionCode(permissionCode);
                        rp.setIsEnabled(isEnabled);
                        inputRole.getRolePermissions().add(rp);
                      });
              return inputRole;
            })
        .flatMap(roleService::create)
        .flatMap(this::toApiResponse);
  }

  /**
   * ロールマスタを一括登録します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "ロールマスタ一括登録", description = "ロールマスタを一括登録します。")
  @PreAuthorize("hasAuthority('role:save')")
  @PostMapping(value = "/roles")
  public Mono<ApiResponse> createAll(
      @Validated @RequestBody Mono<Requests<RoleRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, Role.class)).toList())
        .flatMap(roles -> roleService.create(roles).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * ロールマスタを検索します。
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "ロールマスタ検索", description = "ロールマスタを検索します。")
  @PreAuthorize("hasAuthority('role:read')")
  @GetMapping("/roles")
  public Mono<ApiResponse> search(
      @ModelAttribute SearchRoleRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, RoleCriteria.class);
    return roleService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * ロールマスタを検索します。（POST版）
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "ロールマスタ検索", description = "ロールマスタを検索します。")
  @PreAuthorize("hasAuthority('role:read')")
  @PostMapping("/roles/search")
  public Mono<ApiResponse> searchByPost(
      @RequestBody SearchRoleRequest request, @Parameter(hidden = true) Pageable pageable) {
    return search(request, pageable);
  }

  /**
   * ロールマスタを取得します。
   *
   * @param roleId
   * @return
   */
  @Operation(summary = "ロールマスタ取得", description = "ロールマスタを取得します。")
  @PreAuthorize("hasAuthority('role:read')")
  @GetMapping("/role/{roleId}")
  public Mono<ApiResponse> show(@PathVariable UUID roleId) {
    return roleService.findById(roleId).flatMap(this::toApiResponse);
  }

  /**
   * ロールマスタを更新します。
   *
   * @param roleId
   * @param requestMono
   * @return
   */
  @Operation(summary = "ロールマスタ更新", description = "ロールマスタを更新します。")
  @PreAuthorize("hasAuthority('role:save')")
  @PutMapping("/role/{roleId}")
  public Mono<ApiResponse> update(
      @PathVariable UUID roleId, @Validated @RequestBody Mono<RoleRequest> requestMono) {
    return roleService
        .findById(roleId)
        .zipWith(requestMono)
        .map(
            tuple2 -> {
              val role = tuple2.getT1();
              val request = tuple2.getT2();
              role.setRoleCode(request.getRoleCode());
              role.setRoleName(request.getRoleName());
              role.getRolePermissions()
                  .forEach(
                      rp -> {
                        val permissionCode = rp.getPermissionCode();
                        val isEnabled = request.getPermissions().get(permissionCode);
                        rp.setIsEnabled(isEnabled);
                      });
              return role;
            })
        .flatMap(roleService::update)
        .flatMap(this::toApiResponse);
  }

  /**
   * ロールマスタを一括更新します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "ロールマスタ一括更新", description = "ロールマスタを一括更新します。")
  @PreAuthorize("hasAuthority('role:save')")
  @PutMapping(value = "/roles")
  public Mono<ApiResponse> update(
      @Validated @RequestBody Mono<Requests<RoleRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, Role.class)).toList())
        .flatMap(roles -> roleService.update(roles).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * ロールマスタを削除します。
   *
   * @param roleId
   * @return
   */
  @Operation(summary = "ロールマスタ削除", description = "ロールマスタを削除します。")
  @PreAuthorize("hasAuthority('role:save')")
  @DeleteMapping("/role/{roleId}")
  public Mono<ApiResponse> delete(@PathVariable UUID roleId) {
    return roleService
        .findById(roleId)
        .flatMap(role -> roleService.delete(roleId).thenReturn(role))
        .flatMap(this::toApiResponse);
  }

  /**
   * ロールマスタを一括削除します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "ロールマスタ一括削除", description = "ロールマスタを一括削除します。")
  @PreAuthorize("hasAuthority('role:save')")
  @DeleteMapping(value = "/roles")
  public Mono<ApiResponse> delete(
      @Validated @RequestBody Mono<Requests<DeleteRoleRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(DeleteRoleRequest::getId).toList())
        .flatMap(roleService::delete)
        .thenReturn(createSimpleApiResponse());
  }

  /**
   * CSV出力
   *
   * @param filename
   * @return
   */
  @Operation(summary = "ロールマスタCSV出力", description = "CSVファイルを出力します。")
  @PreAuthorize("hasAuthority('role:read')")
  @GetMapping("/roles/export/{filename:.+\\.csv}")
  public Mono<ResponseEntity<Resource>> downloadCsv(
      @PathVariable String filename, ServerHttpResponse response) {
    return roleService
        .findAll(new RoleCriteria(), Pageable.unpaged())
        .map(
            pages -> {
              val csvList = modelMapper.map(pages.getContent(), toListType(RoleCsv.class));
              val dataBuffer = response.bufferFactory().allocateBuffer(1024);
              CsvUtils.writeCsv(RoleCsv.class, csvList, dataBuffer);
              return new InputStreamResource(dataBuffer.asInputStream(true));
            })
        .map(resource -> toResponseEntity(resource, filename, true));
  }
}
