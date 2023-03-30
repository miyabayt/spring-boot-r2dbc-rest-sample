package com.bigtreetc.sample.r2dbc.controller.mailtemplates;

import static com.bigtreetc.sample.r2dbc.base.util.TypeUtils.toListType;

import com.bigtreetc.sample.r2dbc.base.util.CsvUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.request.Requests;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import com.bigtreetc.sample.r2dbc.domain.model.MailTemplate;
import com.bigtreetc.sample.r2dbc.domain.model.MailTemplateCriteria;
import com.bigtreetc.sample.r2dbc.domain.service.MailTemplateService;
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

@Tag(name = "メールテンプレート")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/system", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class MailTemplateController extends AbstractRestController {

  @NonNull final MailTemplateRequestValidator mailTemplateRequestValidator;

  @NonNull final MailTemplateService mailTemplateService;

  @InitBinder
  public void validatorBinder(WebDataBinder binder) {
    binder.addValidators(mailTemplateRequestValidator);
  }

  /**
   * メールテンプレートを登録します。
   *
   * @param requestMono
   * @return
   */
  @Operation(summary = "メールテンプレート登録", description = "メールテンプレートを登録します。")
  @PreAuthorize("hasAuthority('mailTemplate:save')")
  @PostMapping("/mailTemplate")
  public Mono<ApiResponse> create(@Validated @RequestBody Mono<MailTemplateRequest> requestMono) {
    return requestMono
        .map(request -> modelMapper.map(request, MailTemplate.class))
        .flatMap(mailTemplateService::create)
        .flatMap(this::toApiResponse);
  }

  /**
   * メールテンプレートを一括登録します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "メールテンプレート一括登録", description = "メールテンプレートを一括登録します。")
  @PreAuthorize("hasAuthority('mailTemplate:save')")
  @PostMapping(value = "/mailTemplates")
  public Mono<ApiResponse> createAll(
      @Validated @RequestBody Mono<Requests<MailTemplateRequest>> requestsMono) {
    return requestsMono
        .map(
            requests -> requests.stream().map(f -> modelMapper.map(f, MailTemplate.class)).toList())
        .flatMap(mailTemplates -> mailTemplateService.create(mailTemplates).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * メールテンプレートを検索します。
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "メールテンプレート検索", description = "メールテンプレートを検索します。")
  @PreAuthorize("hasAuthority('mailTemplate:read')")
  @GetMapping("/mailTemplates")
  public Mono<ApiResponse> search(
      @ModelAttribute SearchMailTemplateRequest request,
      @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, MailTemplateCriteria.class);
    return mailTemplateService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * メールテンプレートを検索します。（POST版）
   *
   * @param request
   * @param pageable
   * @return
   */
  @PageableAsQueryParam
  @Operation(summary = "メールテンプレート検索", description = "メールテンプレートを検索します。")
  @PreAuthorize("hasAuthority('mailTemplate:read')")
  @PostMapping("/mailTemplates/search")
  public Mono<ApiResponse> searchByPost(
      @RequestBody SearchMailTemplateRequest request, @Parameter(hidden = true) Pageable pageable) {
    val criteria = modelMapper.map(request, MailTemplateCriteria.class);
    return mailTemplateService.findAll(criteria, pageable).flatMap(this::toApiResponse);
  }

  /**
   * メールテンプレートを取得します。
   *
   * @param mailTemplateId
   * @return
   */
  @Operation(summary = "メールテンプレート取得", description = "メールテンプレートを取得します。")
  @PreAuthorize("hasAuthority('mailTemplate:read')")
  @GetMapping("/mailTemplate/{mailTemplateId}")
  public Mono<ApiResponse> show(@PathVariable UUID mailTemplateId) {
    return mailTemplateService.findById(mailTemplateId).flatMap(this::toApiResponse);
  }

  /**
   * メールテンプレートを更新します。
   *
   * @param mailTemplateId
   * @param requestMono
   * @return
   */
  @Operation(summary = "メールテンプレート更新", description = "メールテンプレートを更新します。")
  @PreAuthorize("hasAuthority('mailTemplate:save')")
  @PutMapping("/mailTemplate/{mailTemplateId}")
  public Mono<ApiResponse> update(
      @PathVariable UUID mailTemplateId,
      @Validated @RequestBody Mono<MailTemplateRequest> requestMono) {
    return mailTemplateService
        .findById(mailTemplateId)
        .zipWith(requestMono)
        .map(
            tuple2 -> {
              val mailTemplate = tuple2.getT1();
              val request = tuple2.getT2();
              modelMapper.map(request, mailTemplate);
              return mailTemplate;
            })
        .flatMap(mailTemplateService::update)
        .flatMap(this::toApiResponse);
  }

  /**
   * メールテンプレートを一括更新します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "メールテンプレート一括更新", description = "メールテンプレートを一括更新します。")
  @PreAuthorize("hasAuthority('mailTemplate:save')")
  @PutMapping(value = "/mailTemplates")
  public Mono<ApiResponse> update(
      @Validated @RequestBody Mono<Requests<MailTemplateRequest>> requestsMono) {
    return requestsMono
        .map(
            requests -> requests.stream().map(f -> modelMapper.map(f, MailTemplate.class)).toList())
        .flatMap(mailTemplates -> mailTemplateService.update(mailTemplates).collectList())
        .flatMap(this::toApiResponse);
  }

  /**
   * メールテンプレートを削除します。
   *
   * @param mailTemplateId
   * @return
   */
  @Operation(summary = "メールテンプレート削除", description = "メールテンプレートを削除します。")
  @PreAuthorize("hasAuthority('mailTemplate:save')")
  @DeleteMapping("/mailTemplate/{mailTemplateId}")
  public Mono<ApiResponse> delete(@PathVariable UUID mailTemplateId) {
    return mailTemplateService
        .findById(mailTemplateId)
        .flatMap(
            mailTemplate -> mailTemplateService.delete(mailTemplateId).thenReturn(mailTemplate))
        .flatMap(this::toApiResponse);
  }

  /**
   * メールテンプレートを一括削除します。
   *
   * @param requestsMono
   * @return
   */
  @Operation(summary = "メールテンプレート一括削除", description = "メールテンプレートを一括削除します。")
  @PreAuthorize("hasAuthority('mailTemplate:save')")
  @DeleteMapping(value = "/mailTemplates")
  public Mono<ApiResponse> delete(
      @Validated @RequestBody Mono<Requests<DeleteMailTemplateRequest>> requestsMono) {
    return requestsMono
        .map(requests -> requests.stream().map(DeleteMailTemplateRequest::getId).toList())
        .flatMap(mailTemplateService::delete)
        .thenReturn(createSimpleApiResponse());
  }

  /**
   * CSV出力
   *
   * @param filename
   * @return
   */
  @Operation(summary = "メールテンプレートCSV出力", description = "CSVファイルを出力します。")
  @PreAuthorize("hasAuthority('mailTemplate:read')")
  @GetMapping("/mailTemplates/export/{filename:.+\\.csv}")
  public Mono<ResponseEntity<Resource>> downloadCsv(
      @PathVariable String filename, ServerHttpResponse response) {
    return mailTemplateService
        .findAll(new MailTemplateCriteria(), Pageable.unpaged())
        .map(
            pages -> {
              val csvList = modelMapper.map(pages.getContent(), toListType(MailTemplateCsv.class));
              val dataBuffer = response.bufferFactory().allocateBuffer(1024);
              CsvUtils.writeCsv(MailTemplateCsv.class, csvList, dataBuffer);
              return new InputStreamResource(dataBuffer.asInputStream(true));
            })
        .map(resource -> toResponseEntity(resource, filename, true));
  }
}
