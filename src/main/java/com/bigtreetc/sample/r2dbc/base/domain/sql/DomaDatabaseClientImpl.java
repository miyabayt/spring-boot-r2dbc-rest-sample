package com.bigtreetc.sample.r2dbc.base.domain.sql;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.template.SqlArgument;
import org.seasar.doma.template.SqlStatement;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DomaDatabaseClientImpl implements DomaDatabaseClient {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final MappingR2dbcConverter converter;

  @NonNull final Dialect dialect;

  @Override
  public <T> Mono<T> one(DomaSqlBuilder sqlBuilder, Class<T> type) {
    SqlStatement sqlStatement = sqlBuilder.dialect(dialect).build();
    String rawSql = sqlStatement.getRawSql();
    DatabaseClient.GenericExecuteSpec executeSpec =
        r2dbcEntityTemplate.getDatabaseClient().sql(rawSql);

    List<SqlArgument> arguments = sqlStatement.getArguments();
    for (int i = 0; i < arguments.size(); i++) {
      executeSpec.bind(i, arguments.get(i).getValue());
    }

    return executeSpec.map((row, rowMetaData) -> converter.read(type, row, rowMetaData)).one();
  }

  @Override
  public <T> Flux<T> all(DomaSqlBuilder sqlBuilder, Class<T> type) {
    SqlStatement sqlStatement = sqlBuilder.dialect(dialect).build();
    String rawSql = sqlStatement.getRawSql();
    DatabaseClient.GenericExecuteSpec executeSpec =
        r2dbcEntityTemplate.getDatabaseClient().sql(rawSql);

    List<SqlArgument> arguments = sqlStatement.getArguments();
    for (int i = 0; i < arguments.size(); i++) {
      SqlArgument argument = arguments.get(i);
      Object value = argument.getValue();
      executeSpec = executeSpec.bind(i, value);
    }

    return executeSpec.map((row, rowMetaData) -> converter.read(type, row, rowMetaData)).all();
  }
}
