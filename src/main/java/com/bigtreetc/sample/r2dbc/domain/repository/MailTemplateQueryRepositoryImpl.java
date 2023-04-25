package com.bigtreetc.sample.r2dbc.domain.repository;

import static com.bigtreetc.sample.r2dbc.base.domain.sql.DomaUtils.toSelectOptions;

import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaDatabaseClient;
import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaSqlBuilder;
import com.bigtreetc.sample.r2dbc.domain.model.MailTemplate;
import com.bigtreetc.sample.r2dbc.domain.model.MailTemplateCriteria;
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
public class MailTemplateQueryRepositoryImpl implements MailTemplateQueryRepository {

  @NonNull final DomaDatabaseClient databaseClient;

  @Override
  public Mono<MailTemplate> findOne(final MailTemplateCriteria criteria) {
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/MailTemplateQueryRepository/findAll.sql")
            .addParameter("criteria", MailTemplateCriteria.class, criteria);

    return databaseClient.one(sqlBuilder, MailTemplate.class);
  }

  @Override
  public Mono<Page<MailTemplate>> findAll(
      final MailTemplateCriteria criteria, final Pageable pageable) {
    val selectOptions = toSelectOptions(pageable);
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/MailTemplateQueryRepository/findAll.sql")
            .addParameter("criteria", MailTemplateCriteria.class, criteria)
            .options(selectOptions);

    return databaseClient
        .allWithCount(sqlBuilder, MailTemplate.class)
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  @Override
  public Flux<MailTemplate> findAll(final MailTemplateCriteria criteria) {
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/MailTemplateQueryRepository/findAll.sql")
            .addParameter("criteria", MailTemplateCriteria.class, criteria);

    return databaseClient.all(sqlBuilder, MailTemplate.class);
  }
}
