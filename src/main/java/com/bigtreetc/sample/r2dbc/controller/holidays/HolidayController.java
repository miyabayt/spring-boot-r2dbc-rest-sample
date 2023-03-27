package com.bigtreetc.sample.r2dbc.controller.holidays;

import static com.bigtreetc.sample.r2dbc.base.util.TypeUtils.toListType;

import com.bigtreetc.sample.r2dbc.base.util.CsvUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.request.Requests;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.model.Holiday;
import com.bigtreetc.sample.r2dbc.domain.model.HolidayCriteria;
import com.bigtreetc.sample.r2dbc.domain.service.HolidayService;
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

@Tag(name = "祝日マスタ")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/system", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class HolidayController extends AbstractRestController {

  @NonNull final HolidayRequestValidator holidayRequestValidator;

  @NonNull final HolidayService holidayService;

  @InitBinder
  public void validatorBinder(WebDataBinder binder) {
    binder.addValidators(holidayRequestValidator);
  }

  /**
   * 祝日マスタを登録します。
   *
   * @param requestMono
   * @return
   */
  @Operation(summary = "祝日マスタ登録", description = "祝日マスタを登録します。")
  @PreAuthorize("hasAuthority('holiday:save')")
  @PostMapping("/holiday")
  public Mono<ApiResponse> create(@Validated @RequestBody Mono<HolidayRequest> requestMono) {
    return requestMono
        .map(request -> modelMapper.map(request, Holiday.class))
        .flatMap(holidayService::create)
        .flatMap(this::toApiResponse);
  }

  /**
   * 祝日マスタを一括登録します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "祝日マスタ一括登録", description = "祝日マスタを一括登録します。")
  @PreAuthorize("hasAuthority('holiday:save')")
  @PostMapping(value = "/holidays")
  public Mono<ApiResponse> createAll(
      @Validated @RequestBody Mono<Requests<HolidayRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, Holiday.class)).toList())
        .flatMap(holidays -> holidayService.create(holidays).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * 祝日マスタを検索します。
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "祝日マスタ検索", description = "祝日マスタを検索します。")
  @PreAuthorize("hasAuthority('holiday:read')")
  @GetMapping("/holidays")
  public Mono<ApiResponse> search(
      @ModelAttribute SearchHolidayRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, HolidayCriteria.class);
    return holidayService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * 祝日マスタを検索します。（POST版）
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "祝日マスタ検索", description = "祝日マスタを検索します。")
  @PreAuthorize("hasAuthority('holiday:read')")
  @PostMapping("/holidays/search")
  public Mono<ApiResponse> searchByPost(
      @RequestBody SearchHolidayRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, HolidayCriteria.class);
    return holidayService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * 祝日マスタを取得します。
   *
   * @param holidayId
   * @return
   */
  @Operation(summary = "祝日マスタ取得", description = "祝日マスタを取得します。")
  @PreAuthorize("hasAuthority('holiday:read')")
  @GetMapping("/holiday/{holidayId}")
  public Mono<ApiResponse> show(@PathVariable UUID holidayId) {
    return holidayService.findById(holidayId).flatMap(this::toApiResponse);
  }

  /**
   * 祝日マスタを更新します。
   *
   * @param holidayId
   * @param requestMono
   * @return
   */
  @Operation(summary = "祝日マスタ更新", description = "祝日マスタを更新します。")
  @PreAuthorize("hasAuthority('holiday:save')")
  @PutMapping("/holiday/{holidayId}")
  public Mono<ApiResponse> update(
      @PathVariable UUID holidayId, @Validated @RequestBody Mono<HolidayRequest> requestMono) {
    return holidayService
        .findById(holidayId)
        .zipWith(requestMono)
        .map(
            tuple2 -> {
              val holiday = tuple2.getT1();
              val request = tuple2.getT2();
              modelMapper.map(request, holiday);
              return holiday;
            })
        .flatMap(holidayService::update)
        .flatMap(this::toApiResponse);
  }

  /**
   * 祝日マスタを一括更新します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "祝日マスタ一括更新", description = "祝日マスタを一括更新します。")
  @PreAuthorize("hasAuthority('holiday:save')")
  @PutMapping(value = "/holidays")
  public Mono<ApiResponse> update(
      @Validated @RequestBody Mono<Requests<HolidayRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, Holiday.class)).toList())
        .flatMap(holidays -> holidayService.update(holidays).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * 祝日マスタを削除します。
   *
   * @param holidayId
   * @return
   */
  @Operation(summary = "祝日マスタ削除", description = "祝日マスタを削除します。")
  @PreAuthorize("hasAuthority('holiday:save')")
  @DeleteMapping("/holiday/{holidayId}")
  public Mono<ApiResponse> delete(@PathVariable UUID holidayId) {
    return holidayService
        .findById(holidayId)
        .flatMap(holiday -> holidayService.delete(holidayId).thenReturn(holiday))
        .flatMap(this::toApiResponse);
  }

  /**
   * 祝日マスタを一括削除します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "祝日マスタ一括削除", description = "祝日マスタを一括削除します。")
  @PreAuthorize("hasAuthority('holiday:save')")
  @DeleteMapping(value = "/holidays")
  public Mono<ApiResponse> delete(
      @Validated @RequestBody Mono<Requests<DeleteHolidayRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(DeleteHolidayRequest::getId).toList())
        .flatMap(holidayService::delete)
        .thenReturn(createSimpleApiResponse());
  }

  /**
   * CSV出力
   *
   * @param filename
   * @return
   */
  @Operation(summary = "祝日マスタCSV出力", description = "CSVファイルを出力します。")
  @PreAuthorize("hasAuthority('holiday:read')")
  @GetMapping("/holidays/export/{filename:.+\\.csv}")
  public Mono<ResponseEntity<Resource>> downloadCsv(
      @PathVariable String filename, ServerHttpResponse response) {
    return holidayService
        .findAll(new HolidayCriteria(), Pageable.unpaged())
        .map(
            pages -> {
              val csvList = modelMapper.map(pages.getContent(), toListType(HolidayCsv.class));
              val dataBuffer = response.bufferFactory().allocateBuffer();
              CsvUtils.writeCsv(HolidayCsv.class, csvList, dataBuffer);
              return new InputStreamResource(dataBuffer.asInputStream(true));
            })
        .map(resource -> toResponseEntity(resource, filename, true));
  }
}
