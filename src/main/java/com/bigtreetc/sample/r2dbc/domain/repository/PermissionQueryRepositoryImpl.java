package com.bigtreetc.sample.r2dbc.domain.repository;

import static com.bigtreetc.sample.r2dbc.base.domain.sql.DomaUtils.toSelectOptions;

import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaDatabaseClient;
import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaSqlBuilder;
import com.bigtreetc.sample.r2dbc.domain.model.Permission;
import com.bigtreetc.sample.r2dbc.domain.model.PermissionCriteria;
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
public class PermissionQueryRepositoryImpl implements PermissionQueryRepository {

  @NonNull final DomaDatabaseClient databaseClient;

  @Override
  public Mono<Permission> findOne(final PermissionCriteria criteria) {
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/PermissionQueryRepository/findAll.sql")
            .addParameter("criteria", PermissionCriteria.class, criteria);

    return databaseClient.one(sqlBuilder, Permission.class);
  }

  @Override
  public Mono<Page<Permission>> findAll(
      final PermissionCriteria criteria, final Pageable pageable) {
    val selectOptions = toSelectOptions(pageable);
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/PermissionQueryRepository/findAll.sql")
            .addParameter("criteria", PermissionCriteria.class, criteria)
            .options(selectOptions);

    return databaseClient
        .allWithCount(sqlBuilder, Permission.class)
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  @Override
  public Flux<Permission> findAll(final PermissionCriteria criteria) {
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/PermissionQueryRepository/findAll.sql")
            .addParameter("criteria", PermissionCriteria.class, criteria);

    return databaseClient.all(sqlBuilder, Permission.class);
  }
}
