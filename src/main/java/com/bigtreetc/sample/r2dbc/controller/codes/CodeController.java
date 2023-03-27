package com.bigtreetc.sample.r2dbc.controller.codes;

import static com.bigtreetc.sample.r2dbc.base.util.TypeUtils.toListType;

import com.bigtreetc.sample.r2dbc.base.util.CsvUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.request.Requests;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.model.Code;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCriteria;
import com.bigtreetc.sample.r2dbc.domain.service.CodeCategoryService;
import com.bigtreetc.sample.r2dbc.domain.service.CodeService;
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

@Tag(name = "コードマスタ")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/system", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CodeController extends AbstractRestController {

  @NonNull final CodeRequestValidator codeRequestValidator;

  @NonNull final CodeCategoryService codeCategoryService;

  @NonNull final CodeService codeService;

  @InitBinder
  public void validatorBinder(WebDataBinder binder) {
    binder.addValidators(codeRequestValidator);
  }

  /**
   * コードマスタを登録します。
   *
   * @param requestMono
   * @return
   */
  @Operation(summary = "コードマスタ登録", description = "コードマスタを登録します。")
  @PreAuthorize("hasAuthority('code:save')")
  @PostMapping("/code")
  public Mono<ApiResponse> create(@Validated @RequestBody Mono<CodeRequest> requestMono) {
    return requestMono
        .map(request -> modelMapper.map(request, Code.class))
        .flatMap(codeService::create)
        .flatMap(this::toApiResponse);
  }

  /**
   * コードマスタを一括登録します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コードマスタ一括登録", description = "コードマスタを一括登録します。")
  @PreAuthorize("hasAuthority('code:save')")
  @PostMapping(value = "/codes")
  public Mono<ApiResponse> createAll(
      @Validated @RequestBody Mono<Requests<CodeRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, Code.class)).toList())
        .flatMap(codes -> codeService.create(codes).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * コードマスタを検索します。
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "コードマスタ検索", description = "コードマスタを検索します。")
  @PreAuthorize("hasAuthority('code:read')")
  @GetMapping("/codes")
  public Mono<ApiResponse> search(
      @ModelAttribute SearchCodeRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, CodeCriteria.class);
    return codeService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * コードマスタを検索します。（POST版）
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "コードマスタ検索", description = "コードマスタを検索します。")
  @PreAuthorize("hasAuthority('code:read')")
  @PostMapping("/codes/search")
  public Mono<ApiResponse> searchByPost(
      @RequestBody SearchCodeRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, CodeCriteria.class);
    return codeService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * コードマスタを取得します。
   *
   * @param codeId
   * @return
   */
  @Operation(summary = "コード取得", description = "コードマスタを取得します。")
  @PreAuthorize("hasAuthority('code:read')")
  @GetMapping("/code/{codeId}")
  public Mono<ApiResponse> show(@PathVariable UUID codeId) {
    return codeService.findById(codeId).flatMap(this::toApiResponse);
  }

  /**
   * コードマスタを更新します。
   *
   * @param codeId
   * @param requestMono
   * @return
   */
  @Operation(summary = "コード更新", description = "コードマスタを更新します。")
  @PreAuthorize("hasAuthority('code:save')")
  @PutMapping("/code/{codeId}")
  public Mono<ApiResponse> update(
      @PathVariable UUID codeId, @Validated @RequestBody Mono<CodeRequest> requestMono) {
    return codeService
        .findById(codeId)
        .zipWith(requestMono)
        .map(
            tuple2 -> {
              val code = tuple2.getT1();
              val request = tuple2.getT2();
              modelMapper.map(request, code);
              return code;
            })
        .flatMap(codeService::update)
        .flatMap(this::toApiResponse);
  }

  /**
   * コードマスタを一括更新します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コード一括更新", description = "コードマスタを一括更新します。")
  @PreAuthorize("hasAuthority('code:save')")
  @PutMapping(value = "/codes")
  public Mono<ApiResponse> update(
      @Validated @RequestBody Mono<Requests<CodeRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(f -> modelMapper.map(f, Code.class)).toList())
        .flatMap(codes -> codeService.update(codes).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * コードマスタを削除します。
   *
   * @param codeId
   * @return
   */
  @Operation(summary = "コード削除", description = "コードマスタを削除します。")
  @PreAuthorize("hasAuthority('code:save')")
  @DeleteMapping("/code/{codeId}")
  public Mono<ApiResponse> delete(@PathVariable UUID codeId) {
    return codeService
        .findById(codeId)
        .flatMap(code -> codeService.delete(codeId).thenReturn(code))
        .flatMap(this::toApiResponse);
  }

  /**
   * コードマスタを一括削除します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コード一括削除", description = "コードマスタを一括削除します。")
  @PreAuthorize("hasAuthority('code:save')")
  @DeleteMapping(value = "/codes")
  public Mono<ApiResponse> delete(
      @Validated @RequestBody Mono<Requests<DeleteCodeRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(DeleteCodeRequest::getId).toList())
        .flatMap(codeService::delete)
        .thenReturn(createSimpleApiResponse());
  }

  /**
   * CSV出力
   *
   * @param filename
   * @return
   */
  @Operation(summary = "コードCSV出力", description = "CSVファイルを出力します。")
  @PreAuthorize("hasAuthority('code:read')")
  @GetMapping("/codes/export/{filename:.+\\.csv}")
  public Mono<ResponseEntity<Resource>> downloadCsv(
      @PathVariable String filename, ServerHttpResponse response) {
    return codeService
        .findAll(new CodeCriteria(), Pageable.unpaged())
        .map(
            pages -> {
              val csvList = modelMapper.map(pages.getContent(), toListType(CodeCsv.class));
              val dataBuffer = response.bufferFactory().allocateBuffer();
              CsvUtils.writeCsv(CodeCsv.class, csvList, dataBuffer);
              return new InputStreamResource(dataBuffer.asInputStream(true));
            })
        .map(resource -> toResponseEntity(resource, filename, true));
  }
}
