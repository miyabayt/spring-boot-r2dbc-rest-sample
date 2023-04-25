package com.bigtreetc.sample.r2dbc.domain.service;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCategoryCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.CodeCategoryRepository;
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

/** コード分類マスタサービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class CodeCategoryService {

  @NonNull final CodeCategoryRepository codeCategoryRepository;

  /**
   * コード分類マスタを検索します。
   *
   * @param criteria
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<CodeCategory>> findAll(
      final CodeCategoryCriteria criteria, final Pageable pageable) {
    Assert.notNull(criteria, "criteria must not be null");
    Assert.notNull(pageable, "pageable must not be null");
    return codeCategoryRepository.findAll(criteria, pageable);
  }

  /**
   * コード分類マスタを検索します。
   *
   * @param criteria
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Flux<CodeCategory> findAll(final CodeCategoryCriteria criteria) {
    Assert.notNull(criteria, "criteria must not be null");
    return codeCategoryRepository.findAll(criteria);
  }

  /**
   * コード分類マスタを取得します。
   *
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<CodeCategory> findOne(final CodeCategoryCriteria criteria) {
    Assert.notNull(criteria, "criteria must not be null");
    return codeCategoryRepository.findOne(criteria);
  }

  /**
   * コード分類マスタを取得します。
   *
   * @param id
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<CodeCategory> findById(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return codeCategoryRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new NoDataFoundException("id=" + id + " のデータが見つかりません。")));
  }

  /**
   * コード分類マスタを登録します。
   *
   * @param codeCategory
   * @return
   */
  public Mono<CodeCategory> create(final CodeCategory codeCategory) {
    Assert.notNull(codeCategory, "codeCategory must not be null");
    codeCategory.setId(UUID.randomUUID());
    return codeCategoryRepository.save(codeCategory);
  }

  /**
   * コード分類マスタを登録します。
   *
   * @param codeCategories
   * @return
   */
  public Flux<CodeCategory> create(final List<CodeCategory> codeCategories) {
    Assert.notNull(codeCategories, "codeCategories must not be null");
    for (val codeCategory : codeCategories) {
      codeCategory.setId(UUID.randomUUID());
    }
    return codeCategoryRepository.saveAll(codeCategories);
  }

  /**
   * コード分類マスタを更新します。
   *
   * @param codeCategory
   * @return
   */
  public Mono<CodeCategory> update(final CodeCategory codeCategory) {
    Assert.notNull(codeCategory, "codeCategory must not be null");
    return codeCategoryRepository.save(codeCategory);
  }

  /**
   * コード分類マスタを更新します。
   *
   * @param codeCategories
   * @return
   */
  public Flux<CodeCategory> update(final List<CodeCategory> codeCategories) {
    Assert.notNull(codeCategories, "codeCategory must not be null");
    return codeCategoryRepository.saveAll(codeCategories);
  }

  /**
   * コード分類マスタを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return codeCategoryRepository.deleteById(id);
  }

  /**
   * コード分類マスタを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return codeCategoryRepository.deleteAllById(ids);
  }
}
