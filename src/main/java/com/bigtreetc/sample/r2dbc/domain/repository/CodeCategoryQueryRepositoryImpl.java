package com.bigtreetc.sample.r2dbc.domain.repository;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCategoryCriteria;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class CodeCategoryQueryRepositoryImpl implements CodeCategoryQueryRepository {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  /**
   * 指定された条件でコード分類を検索します。
   *
   * @param example
   * @param pageable
   * @return
   */
  public Mono<Page<CodeCategory>> findAll(
      final Example<CodeCategoryCriteria> example, final Pageable pageable) {
    val criteria = getCriteria(example);
    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(CodeCategory.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(r2dbcEntityTemplate.count(query, CodeCategory.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  private static List<Criteria> getCriteria(Example<CodeCategoryCriteria> example) {
    val codeCategory = example.getProbe();
    val criteria = new ArrayList<Criteria>();

    if (isNotEmpty(codeCategory.getCategoryCode())) {
      criteria.add(where("category_code").is(codeCategory.getCategoryCode()));
    }
    if (isNotEmpty(codeCategory.getCategoryName())) {
      criteria.add(where("category_name").like("%%%s%%".formatted(codeCategory.getCategoryName())));
    }

    return criteria;
  }
}
