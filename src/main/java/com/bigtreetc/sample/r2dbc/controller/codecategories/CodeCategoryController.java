package com.bigtreetc.sample.r2dbc.controller.codecategories;

import static com.bigtreetc.sample.r2dbc.base.util.TypeUtils.toListType;

import com.bigtreetc.sample.r2dbc.base.util.CsvUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.request.Requests;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCategoryCriteria;
import com.bigtreetc.sample.r2dbc.domain.service.CodeCategoryService;
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

@Tag(name = "コード分類マスタ")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/system", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CodeCategoryController extends AbstractRestController {

  @NonNull final CodeCategoryRequestValidator codeCategoryRequestValidator;

  @NonNull final CodeCategoryService codeCategoryService;

  @InitBinder
  public void validatorBinder(WebDataBinder binder) {
    binder.addValidators(codeCategoryRequestValidator);
  }

  /**
   * コード分類マスタを登録します。
   *
   * @param requestMono
   * @return
   */
  @Operation(summary = "コード分類マスタ登録", description = "コード分類マスタを登録します。")
  @PreAuthorize("hasAuthority('codeCategory:save')")
  @PostMapping("/codeCategory")
  public Mono<ApiResponse> create(@Validated @RequestBody Mono<CodeCategoryRequest> requestMono) {
    return requestMono
        .map(request -> modelMapper.map(request, CodeCategory.class))
        .flatMap(codeCategoryService::create)
        .flatMap(this::toApiResponse);
  }

  /**
   * コード分類マスタを一括登録します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コード分類マスタ一括登録", description = "コード分類マスタを一括登録します。")
  @PreAuthorize("hasAuthority('codeCategory:save')")
  @PostMapping(value = "/codeCategories")
  public Mono<ApiResponse> createAll(
      @Validated @RequestBody Mono<Requests<CodeCategoryRequest>> requestsMono) {
    return requestsMono
        .map(
            requests -> requests.stream().map(f -> modelMapper.map(f, CodeCategory.class)).toList())
        .flatMap(codeCategories -> codeCategoryService.create(codeCategories).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * コード分類マスタを検索します。
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "コード分類マスタ検索", description = "コード分類マスタを検索します。")
  @PreAuthorize("hasAuthority('codeCategory:read')")
  @GetMapping("/codeCategories")
  public Mono<ApiResponse> index(
      @ModelAttribute SearchCodeCategoryRequest request,
      @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, CodeCategoryCriteria.class);
    return codeCategoryService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * コード分類マスタを取得します。
   *
   * @param codeCategoryId
   * @return
   */
  @Operation(summary = "コード分類マスタ取得", description = "コード分類マスタを取得します。")
  @PreAuthorize("hasAuthority('codeCategory:read')")
  @GetMapping("/codeCategory/{codeCategoryId}")
  public Mono<ApiResponse> show(@PathVariable UUID codeCategoryId) {
    return codeCategoryService.findById(codeCategoryId).flatMap(this::toApiResponse);
  }

  /**
   * コード分類マスタを更新します。
   *
   * @param codeCategoryId
   * @param requestMono
   * @return
   */
  @Operation(summary = "コード分類マスタ更新", description = "コード分類マスタを更新します。")
  @PreAuthorize("hasAuthority('codeCategory:save')")
  @PutMapping("/codeCategory/{codeCategoryId}")
  public Mono<ApiResponse> update(
      @PathVariable UUID codeCategoryId,
      @Validated @RequestBody Mono<CodeCategoryRequest> requestMono) {
    return codeCategoryService
        .findById(codeCategoryId)
        .zipWith(requestMono)
        .map(
            tuple2 -> {
              val codeCategory = tuple2.getT1();
              val request = tuple2.getT2();
              modelMapper.map(request, codeCategory);
              return codeCategory;
            })
        .flatMap(codeCategoryService::update)
        .flatMap(this::toApiResponse);
  }

  /**
   * コード分類マスタを一括更新します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コード分類マスタ一括更新", description = "コード分類マスタを一括更新します。")
  @PreAuthorize("hasAuthority('codeCategory:save')")
  @PutMapping(value = "/codeCategories")
  public Mono<ApiResponse> update(
      @Validated @RequestBody Mono<Requests<CodeCategoryRequest>> requestsMono) {
    return requestsMono
        .map(
            requests -> requests.stream().map(f -> modelMapper.map(f, CodeCategory.class)).toList())
        .flatMap(codeCategories -> codeCategoryService.update(codeCategories).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * コード分類マスタを削除します。
   *
   * @param codeCategoryId
   * @return
   */
  @Operation(summary = "コード分類マスタ削除", description = "コード分類マスタを削除します。")
  @PreAuthorize("hasAuthority('codeCategory:save')")
  @DeleteMapping("/codeCategory/{codeCategoryId}")
  public Mono<ApiResponse> delete(@PathVariable UUID codeCategoryId) {
    return codeCategoryService
        .findById(codeCategoryId)
        .flatMap(
            codeCategory -> codeCategoryService.delete(codeCategoryId).thenReturn(codeCategory))
        .flatMap(this::toApiResponse);
  }

  /**
   * コード分類マスタを一括削除します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コード分類マスタ一括削除", description = "コード分類マスタを一括削除します。")
  @PreAuthorize("hasAuthority('codeCategory:save')")
  @DeleteMapping(value = "/codeCategories")
  public Mono<ApiResponse> delete(
      @Validated @RequestBody Mono<Requests<DeleteCodeCategoryRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(DeleteCodeCategoryRequest::getId).toList())
        .flatMap(codeCategoryService::delete)
        .thenReturn(createSimpleApiResponse());
  }

  /**
   * CSV出力
   *
   * @param filename
   * @return
   */
  @Operation(summary = "コード分類マスタCSV出力", description = "CSVファイルを出力します。")
  @PreAuthorize("hasAuthority('codeCategory:read')")
  @GetMapping("/codeCategories/export/{filename:.+\\.csv}")
  public Mono<ResponseEntity<Resource>> downloadCsv(
      @PathVariable String filename, ServerHttpResponse response) {
    return codeCategoryService
        .findAll(new CodeCategoryCriteria(), Pageable.unpaged())
        .map(
            pages -> {
              val csvList = modelMapper.map(pages.getContent(), toListType(CodeCategoryCsv.class));
              val dataBuffer = response.bufferFactory().allocateBuffer();
              CsvUtils.writeCsv(CodeCategoryCsv.class, csvList, dataBuffer);
              return new InputStreamResource(dataBuffer.asInputStream(true));
            })
        .map(resource -> toResponseEntity(resource, filename, true));
  }
}
