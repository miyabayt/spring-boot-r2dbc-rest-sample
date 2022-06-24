package com.bigtreetc.sample.r2dbc.domain.service.system;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isEquals;
import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.system.Code;
import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.system.CodeCategoryRepository;
import com.bigtreetc.sample.r2dbc.domain.repository.system.CodeRepository;
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
import reactor.util.function.Tuple2;

/** コードサービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class CodeService {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final CodeRepository codeRepository;

  @NonNull final CodeCategoryRepository codeCategoryRepository;

  /**
   * コードを検索します。
   *
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<Code>> findAll(CodeCriteria code, Pageable pageable) {
    Assert.notNull(code, "code must not be null");
    val criteria = new ArrayList<Criteria>();
    if (isNotEmpty(code.getCategoryCode())) {
      criteria.add(where("category_code").is(code.getCategoryCode()));
    }
    if (isNotEmpty(code.getCodeValue())) {
      criteria.add(where("code_value").like(code.getCodeValue()));
    }
    if (isNotEmpty(code.getCodeName())) {
      criteria.add(where("code_name").like("%%%s%%".formatted(code.getCodeName())));
    }

    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(Code.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(codeCategoryRepository.findAll().collectList())
        .map(this::mergeCodesAndCodeCategories)
        .zipWith(r2dbcEntityTemplate.count(query, Code.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  /**
   * コードを取得します。
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
   * コードを取得します。
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
   * コードを登録します。
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
   * コードを登録します。
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
   * コードを更新します。
   *
   * @param code
   * @return
   */
  public Mono<Code> update(final Code code) {
    Assert.notNull(code, "code must not be null");
    return codeRepository.save(code);
  }

  /**
   * コードを更新します。
   *
   * @param codes
   * @return
   */
  public Flux<Code> update(final List<Code> codes) {
    Assert.notNull(codes, "code must not be null");
    return codeRepository.saveAll(codes);
  }

  /**
   * コードを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return codeRepository.deleteById(id);
  }

  /**
   * コードを削除します。
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
