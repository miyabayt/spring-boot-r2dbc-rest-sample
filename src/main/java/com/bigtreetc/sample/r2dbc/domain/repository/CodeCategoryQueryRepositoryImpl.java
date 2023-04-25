package com.bigtreetc.sample.r2dbc.domain.repository;

import static com.bigtreetc.sample.r2dbc.base.domain.sql.DomaUtils.toSelectOptions;

import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaDatabaseClient;
import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaSqlBuilder;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.model.CodeCategoryCriteria;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class CodeCategoryQueryRepositoryImpl implements CodeCategoryQueryRepository {

  @NonNull final DomaDatabaseClient databaseClient;

  @Override
  public Mono<CodeCategory> findOne(final CodeCategoryCriteria criteria) {
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/CodeCategoryQueryRepository/findAll.sql")
            .addParameter("criteria", CodeCategoryCriteria.class, criteria);

    return databaseClient.one(sqlBuilder, CodeCategory.class);
  }

  @Override
  public Mono<Page<CodeCategory>> findAll(
      final CodeCategoryCriteria criteria, final Pageable pageable) {
    val selectOptions = toSelectOptions(pageable);
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/CodeCategoryQueryRepository/findAll.sql")
            .addParameter("criteria", CodeCategoryCriteria.class, criteria)
            .options(selectOptions);

    return databaseClient
        .allWithCount(sqlBuilder, CodeCategory.class)
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  @Override
  public Flux<CodeCategory> findAll(final CodeCategoryCriteria criteria) {
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/CodeCategoryQueryRepository/findAll.sql")
            .addParameter("criteria", CodeCategoryCriteria.class, criteria);

    return databaseClient.all(sqlBuilder, CodeCategory.class);
  }
}
