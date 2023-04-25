package com.bigtreetc.sample.r2dbc.base.domain.sql;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.template.SqlArgument;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RequiredArgsConstructor
public class DomaDatabaseClientImpl implements DomaDatabaseClient {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final MappingR2dbcConverter converter;

  @NonNull final Dialect dialect;

  @Override
  public <T> Mono<T> one(DomaSqlBuilder sqlBuilder, Class<T> type) {
    val sqlStatement = sqlBuilder.dialect(dialect).build();
    val rawSql = sqlStatement.getRawSql();
    List<SqlArgument> arguments = sqlStatement.getArguments();
    GenericExecuteSpec executeSpec = getGenericExecuteSpec(rawSql, arguments);
    return executeSpec.map((row, rowMetaData) -> converter.read(type, row, rowMetaData)).one();
  }

  @Override
  public <T> Flux<T> all(DomaSqlBuilder sqlBuilder, Class<T> type) {
    val sqlStatement = sqlBuilder.dialect(dialect).build();
    val rawSql = sqlStatement.getRawSql();
    List<SqlArgument> arguments = sqlStatement.getArguments();
    GenericExecuteSpec executeSpec = getGenericExecuteSpec(rawSql, arguments);
    return executeSpec.map((row, rowMetaData) -> converter.read(type, row, rowMetaData)).all();
  }

  @Override
  public <T> Mono<Tuple2<List<T>, Long>> allWithCount(DomaSqlBuilder sqlBuilder, Class<T> type) {
    val sqlStatement = sqlBuilder.dialect(dialect).build();
    val countRawSql = sqlStatement.getCountRawSql();
    List<SqlArgument> arguments = sqlStatement.getArguments();
    GenericExecuteSpec countExecuteSpec = getGenericExecuteSpec(countRawSql, arguments);
    return all(sqlBuilder, type)
        .collectList()
        .zipWith(countExecuteSpec.map((row, rowMetaData) -> row.get(0, Long.class)).one());
  }

  private DatabaseClient.GenericExecuteSpec getGenericExecuteSpec(
      String rawSql, List<SqlArgument> arguments) {
    DatabaseClient.GenericExecuteSpec executeSpec =
        r2dbcEntityTemplate.getDatabaseClient().sql(rawSql);

    if (rawSql.contains("?")) {
      for (int i = 0; i < arguments.size(); i++) {
        SqlArgument argument = arguments.get(i);
        Object value = argument.getValue();
        executeSpec = executeSpec.bind(i, value);
      }
    }

    return executeSpec;
  }
}
