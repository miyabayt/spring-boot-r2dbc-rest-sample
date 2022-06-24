package com.bigtreetc.sample.r2dbc.controller.system.uploadfiles;

import com.bigtreetc.sample.r2dbc.base.domain.helper.FileHelper;
import com.bigtreetc.sample.r2dbc.base.util.FileUtils;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.AbstractRestController;
import com.bigtreetc.sample.r2dbc.base.web.controller.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.file.Paths;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "ファイルアップロード")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/system", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UploadFileController extends AbstractRestController implements InitializingBean {

  @Value(
      "${application.fileUploadLocation:#{systemProperties['java.io.tmpdir']}}") // 設定ファイルに定義されたアップロード先を取得する
  String fileUploadLocation;

  @NonNull final FileHelper fileHelper;

  /**
   * ファイルの一覧を返します。
   *
   * @return
   */
  @Operation(summary = "ファイル一覧取得", description = "ファイルの一覧を取得します。")
  @PreAuthorize("hasAuthority('uploadFile')")
  @GetMapping("/files")
  public Mono<ApiResponse> listFiles() {
    return Mono.fromCallable(
            () -> {
              // ファイル名のリストを作成する
              val location = Paths.get(fileUploadLocation);
              return fileHelper.listAllFiles(location).stream()
                  .map(path -> path.getFileName().toString())
                  .toList();
            })
        .flatMap(this::toApiResponse);
  }

  /**
   * ファイル内容をレスポンスします。
   *
   * @param filename
   * @return
   */
  @Operation(summary = "ファイル内容表示", description = "ファイルの内容を表示します。")
  @PreAuthorize("hasAuthority('uploadFile')")
  @GetMapping(path = "/file/{filename:.+}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity<Resource>> serveFile(@PathVariable String filename) {
    return Mono.fromCallable(() -> fileHelper.loadFile(Paths.get(fileUploadLocation), filename))
        .map(resource -> toResponseEntity(resource, filename));
  }

  /**
   * ファイルをダウンロードします。
   *
   * @param filename
   * @return
   */
  @Operation(summary = "ファイルダウンロード", description = "ファイルをダウンロードします。")
  @PreAuthorize("hasAuthority('uploadFile')")
  @GetMapping(
      path = "/file/download/{filename:.+}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity<Resource>> downloadFile(@PathVariable String filename) {
    return Mono.fromCallable(() -> fileHelper.loadFile(Paths.get(fileUploadLocation), filename))
        .map(resource -> toResponseEntity(resource, filename, true));
  }

  /**
   * ファイルをアップロードします。
   *
   * @param file
   * @return
   */
  @Operation(summary = "ファイルアップロード", description = "ファイルをアップロードします。")
  @PreAuthorize("hasAuthority('uploadFile')")
  @PostMapping(path = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ApiResponse>> uploadFile(@RequestPart("file") FilePart file) {
    return file.content()
        .doOnNext(
            dataBuffer -> {
              val filename = file.filename();
              val inputStream = dataBuffer.asInputStream(true);
              FileUtils.saveFile(Paths.get(fileUploadLocation), filename, inputStream);
            })
        .then(Mono.just(ResponseEntity.ok(createSimpleApiResponse())));
  }

  @Override
  public void afterPropertiesSet() {
    // アップロードディレクトリ
    val location = Paths.get(fileUploadLocation);

    // ディレクトリがない場合は作成する
    FileUtils.createDirectories(location);
  }
}
