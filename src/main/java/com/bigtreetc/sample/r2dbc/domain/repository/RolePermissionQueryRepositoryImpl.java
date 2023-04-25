package com.bigtreetc.sample.r2dbc.domain.repository;

import static com.bigtreetc.sample.r2dbc.base.domain.sql.DomaUtils.toSelectOptions;

import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaDatabaseClient;
import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaSqlBuilder;
import com.bigtreetc.sample.r2dbc.domain.model.RolePermission;
import com.bigtreetc.sample.r2dbc.domain.model.RolePermissionCriteria;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class RolePermissionQueryRepositoryImpl implements RolePermissionQueryRepository {

  @NonNull final DomaDatabaseClient databaseClient;

  @Override
  public Mono<RolePermission> findOne(final RolePermissionCriteria criteria) {
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/RolePermissionQueryRepository/findAll.sql")
            .addParameter("criteria", RolePermissionCriteria.class, criteria);

    return databaseClient.one(sqlBuilder, RolePermission.class);
  }

  @Override
  public Mono<Page<RolePermission>> findAll(
      final RolePermissionCriteria criteria, final Pageable pageable) {
    val selectOptions = toSelectOptions(pageable);
    val sqlBuilder =
        DomaSqlBuilder.builder()
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/repository/RolePermissionQueryRepository/findAll.sql")
            .addParameter("criteria", RolePermissionCriteria.class, criteria)
            .options(selectOptions);

    return databaseClient
        .allWithCount(sqlBuilder, RolePermission.class)
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }
}
