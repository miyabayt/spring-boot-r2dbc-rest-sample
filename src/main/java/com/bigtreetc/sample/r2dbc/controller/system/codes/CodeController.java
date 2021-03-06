package com.bigtreetc.sample.r2dbc.controller.system.codes;

import static com.bigtreetc.sample.r2dbc.base.util.TypeUtils.toListType;

import com.bigtreetc.sample.r2dbc.base.util.CsvUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.request.Requests;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.model.system.Code;
import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCriteria;
import com.bigtreetc.sample.r2dbc.domain.service.system.CodeCategoryService;
import com.bigtreetc.sample.r2dbc.domain.service.system.CodeService;
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

@Tag(name = "コード")
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
   * コードを登録します。
   *
   * @param requestMono
   * @return
   */
  @Operation(summary = "コード登録", description = "コードを登録します。")
  @PreAuthorize("hasAuthority('code:save')")
  @PostMapping("/code")
  public Mono<ApiResponse> create(@Validated @RequestBody Mono<CodeRequest> requestMono) {
    return requestMono
        .map(request -> modelMapper.map(request, Code.class))
        .flatMap(codeService::create)
        .flatMap(this::toApiResponse);
  }

  /**
   * コードを一括登録します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コード一括登録", description = "コードを一括登録します。")
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
   * コードを検索します。
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "コード検索", description = "コードを検索します。")
  @PreAuthorize("hasAuthority('code:read')")
  @GetMapping("/codes")
  public Mono<ApiResponse> index(
      @ModelAttribute SearchCodeRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, CodeCriteria.class);
    return codeService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * コードを取得します。
   *
   * @param codeId
   * @return
   */
  @Operation(summary = "コード取得", description = "コードを取得します。")
  @PreAuthorize("hasAuthority('code:read')")
  @GetMapping("/code/{codeId}")
  public Mono<ApiResponse> show(@PathVariable UUID codeId) {
    return codeService.findById(codeId).flatMap(this::toApiResponse);
  }

  /**
   * コードを更新します。
   *
   * @param codeId
   * @param requestMono
   * @return
   */
  @Operation(summary = "コード更新", description = "コードを更新します。")
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
   * コードを一括更新します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コード一括更新", description = "コードを一括更新します。")
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
   * コードを削除します。
   *
   * @param codeId
   * @return
   */
  @Operation(summary = "コード削除", description = "コードを削除します。")
  @PreAuthorize("hasAuthority('code:save')")
  @DeleteMapping("/code/{codeId}")
  public Mono<ApiResponse> delete(@PathVariable UUID codeId) {
    return codeService
        .findById(codeId)
        .flatMap(code -> codeService.delete(codeId).thenReturn(code))
        .flatMap(this::toApiResponse);
  }

  /**
   * コードを一括削除します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "コード一括削除", description = "コードを一括削除します。")
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
