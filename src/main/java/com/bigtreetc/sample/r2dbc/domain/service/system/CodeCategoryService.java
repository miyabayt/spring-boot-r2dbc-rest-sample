package com.bigtreetc.sample.r2dbc.domain.service.system;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCategoryCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.system.CodeCategoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** コード分類サービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class CodeCategoryService {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final CodeCategoryRepository codeCategoryRepository;

  /**
   * コード分類を検索します。
   *
   * @param codeCategory
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<CodeCategory>> findAll(
      final CodeCategoryCriteria codeCategory, final Pageable pageable) {
    Assert.notNull(codeCategory, "codeCategory must not be null");

    val criteria = new ArrayList<Criteria>();
    if (isNotEmpty(codeCategory.getCategoryCode())) {
      criteria.add(where("category_code").is(codeCategory.getCategoryCode()));
    }
    if (isNotEmpty(codeCategory.getCategoryName())) {
      criteria.add(where("category_name").like("%%%s%%".formatted(codeCategory.getCategoryName())));
    }

    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(CodeCategory.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(r2dbcEntityTemplate.count(query, CodeCategory.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  /**
   * コード分類を取得します。
   *
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<CodeCategory> findOne(final CodeCategoryCriteria codeCategory) {
    Assert.notNull(codeCategory, "criteria must not be null");
    return codeCategoryRepository.findOne(Example.of(codeCategory));
  }

  /**
   * コード分類を取得します。
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
   * コード分類を登録します。
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
   * コード分類を登録します。
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
   * コード分類を更新します。
   *
   * @param codeCategory
   * @return
   */
  public Mono<CodeCategory> update(final CodeCategory codeCategory) {
    Assert.notNull(codeCategory, "codeCategory must not be null");
    return codeCategoryRepository.save(codeCategory);
  }

  /**
   * コード分類を更新します。
   *
   * @param codeCategories
   * @return
   */
  public Flux<CodeCategory> update(final List<CodeCategory> codeCategories) {
    Assert.notNull(codeCategories, "codeCategory must not be null");
    return codeCategoryRepository.saveAll(codeCategories);
  }

  /**
   * コード分類を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return codeCategoryRepository.deleteById(id);
  }

  /**
   * コード分類を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return codeCategoryRepository.deleteAllById(ids);
  }
}
