package com.bigtreetc.sample.r2dbc.controller.system.staffs;

import static com.bigtreetc.sample.r2dbc.base.util.TypeUtils.toListType;
import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;

import com.bigtreetc.sample.r2dbc.base.util.CsvUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.request.Requests;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.model.system.Staff;
import com.bigtreetc.sample.r2dbc.domain.model.system.StaffCriteria;
import com.bigtreetc.sample.r2dbc.domain.service.system.StaffService;
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

@Tag(name = "担当者")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/system", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class StaffController extends AbstractRestController {

  @NonNull final StaffRequestValidator staffRequestValidator;

  @NonNull final StaffService staffService;

  @NonNull final PasswordEncoder passwordEncoder;

  @InitBinder
  public void validatorBinder(WebDataBinder binder) {
    binder.addValidators(staffRequestValidator);
  }

  /**
   * 担当者を登録します。
   *
   * @param requestMono
   * @return
   */
  @Operation(summary = "担当者登録", description = "担当者を登録します。")
  @PreAuthorize("hasAuthority('staff:save')")
  @PostMapping("/staff")
  public Mono<ApiResponse> create(@Validated @RequestBody Mono<StaffRequest> requestMono) {
    return requestMono
        .map(
            request -> {
              val inputStaff = modelMapper.map(request, Staff.class);
              val password = request.getPassword();

              // パスワードをハッシュ化する
              inputStaff.setPassword(passwordEncoder.encode(password));
              return inputStaff;
            })
        .flatMap(staffService::create)
        .flatMap(this::toApiResponse);
  }

  /**
   * 担当者を一括登録します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "担当者一括登録", description = "担当者を一括登録します。")
  @PreAuthorize("hasAuthority('staff:save')")
  @PostMapping(value = "/staffs")
  public Mono<ApiResponse> createAll(
      @Validated @RequestBody Mono<Requests<StaffRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, Staff.class)).toList())
        .flatMap(staffs -> staffService.create(staffs).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * 担当者を検索します。
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "担当者検索", description = "担当者を検索します。")
  @PreAuthorize("hasAuthority('staff:read')")
  @GetMapping("/staffs")
  public Mono<ApiResponse> index(
      @ModelAttribute SearchStaffRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, StaffCriteria.class);
    return staffService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * 担当者を取得します。
   *
   * @param staffId
   * @return
   */
  @Operation(summary = "担当者取得", description = "担当者を取得します。")
  @PreAuthorize("hasAuthority('staff:read')")
  @GetMapping("/staff/{staffId}")
  public Mono<ApiResponse> show(@PathVariable UUID staffId) {
    return staffService.findById(staffId).flatMap(this::toApiResponse);
  }

  /**
   * 担当者を更新します。
   *
   * @param staffId
   * @param requestMono
   * @return
   */
  @Operation(summary = "担当者更新", description = "担当者を更新します。")
  @PreAuthorize("hasAuthority('staff:save')")
  @PutMapping("/staff/{staffId}")
  public Mono<ApiResponse> update(
      @PathVariable UUID staffId, @Validated @RequestBody Mono<StaffRequest> requestMono) {
    return staffService
        .findById(staffId)
        .zipWith(requestMono)
        .map(
            tuple2 -> {
              val staff = tuple2.getT1();
              val request = tuple2.getT2();
              modelMapper.map(request, staff);
              val password = staff.getPassword();
              if (isNotEmpty(password)) {
                staff.setPassword(passwordEncoder.encode(password));
              }
              return staff;
            })
        .flatMap(staffService::update)
        .flatMap(this::toApiResponse);
  }

  /**
   * 担当者を一括更新します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "担当者一括更新", description = "担当者を一括更新します。")
  @PreAuthorize("hasAuthority('staff:save')")
  @PutMapping(value = "/staffs")
  public Mono<ApiResponse> update(
      @Validated @RequestBody Mono<Requests<StaffRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, Staff.class)).toList())
        .flatMap(staffs -> staffService.update(staffs).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * 担当者を削除します。
   *
   * @param staffId
   * @return
   */
  @Operation(summary = "担当者削除", description = "担当者を削除します。")
  @PreAuthorize("hasAuthority('staff:save')")
  @DeleteMapping("/staff/{staffId}")
  public Mono<ApiResponse> delete(@PathVariable UUID staffId) {
    return staffService
        .findById(staffId)
        .flatMap(staff -> staffService.delete(staffId).thenReturn(staff))
        .flatMap(this::toApiResponse);
  }

  /**
   * 担当者を一括削除します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "担当者一括削除", description = "担当者を一括削除します。")
  @PreAuthorize("hasAuthority('staff:save')")
  @DeleteMapping(value = "/staffs")
  public Mono<ApiResponse> delete(
      @Validated @RequestBody Mono<Requests<DeleteStaffRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(DeleteStaffRequest::getId).toList())
        .flatMap(staffService::delete)
        .thenReturn(createSimpleApiResponse());
  }

  /**
   * CSV出力
   *
   * @param filename
   * @return
   */
  @Operation(summary = "担当者CSV出力", description = "CSVファイルを出力します。")
  @PreAuthorize("hasAuthority('staff:read')")
  @GetMapping("/staffs/export/{filename:.+\\.csv}")
  public Mono<ResponseEntity<Resource>> downloadCsv(
      @PathVariable String filename, ServerHttpResponse response) {
    return staffService
        .findAll(new StaffCriteria(), Pageable.unpaged())
        .map(
            pages -> {
              val csvList = modelMapper.map(pages.getContent(), toListType(StaffCsv.class));
              val dataBuffer = response.bufferFactory().allocateBuffer();
              CsvUtils.writeCsv(StaffCsv.class, csvList, dataBuffer);
              return new InputStreamResource(dataBuffer.asInputStream(true));
            })
        .map(resource -> toResponseEntity(resource, filename, true));
  }
}
