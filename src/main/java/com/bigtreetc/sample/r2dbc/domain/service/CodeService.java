package com.bigtreetc.sample.r2dbc.domain.service;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isEquals;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.*;
import com.bigtreetc.sample.r2dbc.domain.repository.CodeCategoryRepository;
import com.bigtreetc.sample.r2dbc.domain.repository.CodeRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/** コードサービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class CodeService {

  @NonNull final CodeRepository codeRepository;

  @NonNull final CodeCategoryRepository codeCategoryRepository;

  /**
   * コードマスタを検索します。
   *
   * @param criteria
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<Code>> findAll(final CodeCriteria criteria, final Pageable pageable) {
    Assert.notNull(criteria, "criteria must not be null");
    Assert.notNull(pageable, "pageable must not be null");
    return codeRepository.findAll(criteria, pageable);
  }

  /**
   * コードマスタを検索します。
   *
   * @param criteria
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Flux<Code> findAll(final CodeCriteria criteria) {
    Assert.notNull(criteria, "criteria must not be null");
    return codeRepository.findAll(criteria);
  }

  /**
   * コードマスタを取得します。
   *
   * @param criteria
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Code> findOne(CodeCriteria criteria) {
    Assert.notNull(criteria, "criteria must not be null");
    return codeRepository
        .findOne(criteria)
        .zipWith(codeCategoryRepository.findAll().collectList())
        .map(this::mergeCodeAndCodeCategories);
  }

  /**
   * コードマスタを取得します。
   *
   * @param id
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
   * コードマスタを追加します。
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
   * コードマスタを登録します。
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
   * コードマスタを更新します。
   *
   * @param code
   * @return
   */
  public Mono<Code> update(final Code code) {
    Assert.notNull(code, "code must not be null");
    return codeRepository.save(code);
  }

  /**
   * コードマスタを更新します。
   *
   * @param codes
   * @return
   */
  public Flux<Code> update(final List<Code> codes) {
    Assert.notNull(codes, "code must not be null");
    return codeRepository.saveAll(codes);
  }

  /**
   * コードマスタを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return codeRepository.deleteById(id);
  }

  /**
   * コードマスタを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return codeRepository.deleteAllById(ids);
  }

  private Code mergeCodeAndCodeCategories(Tuple2<Code, List<CodeCategory>> tuple2) {
    val code = tuple2.getT1();
    val codeCategories = tuple2.getT2();
    codeCategories.stream()
        .filter(cc -> isEquals(cc.getCategoryCode(), code.getCategoryCode()))
        .findFirst()
        .ifPresent(cc -> code.setCategoryName(cc.getCategoryName()));
    return code;
  }
}
