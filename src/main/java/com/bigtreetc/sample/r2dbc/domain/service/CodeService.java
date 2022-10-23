package com.bigtreetc.sample.r2dbc.domain.service;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isEquals;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.Code;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.CodeCategoryRepository;
import com.bigtreetc.sample.r2dbc.domain.repository.CodeRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/** コード定義サービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class CodeService {

  @NonNull final CodeRepository codeRepository;

  @NonNull final CodeCategoryRepository codeCategoryRepository;

  /**
   * コード定義を検索します。
   *
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<Code>> findAll(CodeCriteria code, Pageable pageable) {
    Assert.notNull(code, "code must not be null");
    Assert.notNull(pageable, "pageable must not be null");
    return codeRepository.findAll(Example.of(code), pageable);
  }

  /**
   * コード定義を取得します。
   *
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Code> findOne(CodeCriteria code) {
    Assert.notNull(code, "code must not be null");
    return codeRepository
        .findOne(Example.of(code))
        .zipWith(codeCategoryRepository.findAll().collectList())
        .map(this::mergeCodeAndCodeCategories);
  }

  /**
   * コード定義を取得します。
   *
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Code> findById(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return codeRepository
        .findById(id)
        .zipWith(codeCategoryRepository.findAll().collectList())
        .map(this::mergeCodeAndCodeCategories)
        .switchIfEmpty(Mono.error(new NoDataFoundException("id=" + id + " のデータが見つかりません。")));
  }

  /**
   * コード定義を登録します。
   *
   * @param code
   * @return
   */
  public Mono<Code> create(final Code code) {
    Assert.notNull(code, "code must not be null");
    code.setId(UUID.randomUUID());
    return codeRepository.save(code);
  }

  /**
   * コード定義を登録します。
   *
   * @param codes
   * @return
   */
  public Flux<Code> create(final List<Code> codes) {
    Assert.notNull(codes, "codes must not be null");
    for (val code : codes) {
      code.setId(UUID.randomUUID());
    }
    return codeRepository.saveAll(codes);
  }

  /**
   * コード定義を更新します。
   *
   * @param code
   * @return
   */
  public Mono<Code> update(final Code code) {
    Assert.notNull(code, "code must not be null");
    return codeRepository.save(code);
  }

  /**
   * コード定義を更新します。
   *
   * @param codes
   * @return
   */
  public Flux<Code> update(final List<Code> codes) {
    Assert.notNull(codes, "code must not be null");
    return codeRepository.saveAll(codes);
  }

  /**
   * コード定義を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return codeRepository.deleteById(id);
  }

  /**
   * コード定義を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return codeRepository.deleteAllById(ids);
  }

  private List<Code> mergeCodesAndCodeCategories(Tuple2<List<Code>, List<CodeCategory>> tuple2) {
    val codes = tuple2.getT1();
    val codeCategories = tuple2.getT2();
    codes.forEach(
        c ->
            codeCategories.stream()
                .filter(cc -> isEquals(cc.getCategoryCode(), c.getCategoryCode()))
                .findFirst()
                .ifPresent(cc -> c.setCategoryName(cc.getCategoryName())));
    return codes;
  }

  private Code mergeCodeAndCodeCategories(Tuple2<? extends Code, List<CodeCategory>> tuple2) {
    val code = tuple2.getT1();
    val codeCategories = tuple2.getT2();
    codeCategories.stream()
        .filter(cc -> isEquals(cc.getCategoryCode(), code.getCategoryCode()))
        .findFirst()
        .ifPresent(cc -> code.setCategoryName(cc.getCategoryName()));
    return code;
  }
}
