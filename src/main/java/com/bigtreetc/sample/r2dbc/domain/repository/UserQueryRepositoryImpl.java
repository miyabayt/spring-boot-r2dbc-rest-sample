package com.bigtreetc.sample.r2dbc.domain.repository;

import static com.bigtreetc.sample.r2dbc.base.util.DomaUtils.toSelectOptions;

import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaSqlFileSelectQueryBuilder;
import com.bigtreetc.sample.r2dbc.domain.model.user.User;
import com.bigtreetc.sample.r2dbc.domain.model.user.UserCriteria;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class UserQueryRepositoryImpl implements UserQueryRepository {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final MappingR2dbcConverter converter;

  /**
   * 指定された条件でユーザを検索します。
   *
   * @param example
   * @param pageable
   * @return
   */
  public Mono<Page<User>> findAll(final Example<UserCriteria> example, final Pageable pageable) {
    val userCriteria = example.getProbe();
    val selectSql =
        DomaSqlFileSelectQueryBuilder.builder()
            .dialect(new MysqlDialect())
            .sqlFilePath(
                "META-INF/com/bigtreetc/sample/r2dbc/domain/service/system/userService/findAll.sql")
            .addParameter("criteria", UserCriteria.class, userCriteria)
            .options(toSelectOptions(pageable))
            .build();

    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(selectSql)
        .map((row, rowMetaData) -> converter.read(User.class, row, rowMetaData))
        .all()
        .collectList()
        .zipWith(
            r2dbcEntityTemplate
                .getDatabaseClient()
                .sql("SELECT FOUND_ROWS()")
                .map((row, rowMetaData) -> row.get(0, Long.class))
                .one())
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }
}
