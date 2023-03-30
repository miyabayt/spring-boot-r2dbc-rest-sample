package com.bigtreetc.sample.r2dbc.base.domain.sql;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.seasar.doma.internal.expr.ExpressionEvaluator;
import org.seasar.doma.internal.expr.Value;
import org.seasar.doma.internal.jdbc.sql.NodePreparedSqlBuilder;
import org.seasar.doma.internal.jdbc.sql.SqlParser;
import org.seasar.doma.jdbc.*;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.template.SqlArgument;
import org.seasar.doma.template.SqlStatement;
import org.seasar.doma.wrapper.Wrapper;

public class DomaSqlBuilder {

  private static final Map<String, String> SQL_CACHE = new ConcurrentHashMap<>();

  private Map<String, Value> parameters = new HashMap<>();

  private SelectOptions options = SelectOptions.get();

  private String sqlFilePath;

  private Dialect dialect;

  public static DomaSqlBuilder builder() {
    return new DomaSqlBuilder();
  }

  public DomaSqlBuilder options(SelectOptions options) {
    this.options = options;
    return this;
  }

  public DomaSqlBuilder sqlFilePath(String sqlFilePath) {
    this.sqlFilePath = sqlFilePath;
    return this;
  }

  public DomaSqlBuilder dialect(Dialect dialect) {
    this.dialect = dialect;
    return this;
  }

  public <T> DomaSqlBuilder addParameter(String name, Class<?> type, Object value) {
    this.parameters.put(name, new Value(type, value));
    return this;
  }

  public DomaSqlStatement build() {
    String sqlTemplate = getSqlTemplate(this.sqlFilePath);
    SqlParser parser = new SqlParser(sqlTemplate);
    SqlNode node = parser.parse();
    SqlNode transformedSqlNode = this.dialect.transformSelectSqlNode(node, this.options);
    SqlNode countSqlNode = this.dialect.transformSelectSqlNodeForGettingCount(node);
    NodePreparedSqlBuilder builder = createNodePreparedSqlBuilder();
    PreparedSql preparedSql = builder.build(transformedSqlNode, Function.identity());
    PreparedSql countPreparedSql = builder.build(countSqlNode, Function.identity());
    SqlStatement sqlStatement = toSqlStatement(preparedSql);
    return new DomaSqlStatement(sqlStatement, countPreparedSql);
  }

  private NodePreparedSqlBuilder createNodePreparedSqlBuilder() {
    Config config =
        new Config() {
          @Override
          public DataSource getDataSource() {
            throw new UnsupportedOperationException();
          }

          @Override
          public Dialect getDialect() {
            return dialect;
          }
        };

    ExpressionEvaluator evaluator =
        new ExpressionEvaluator(
            this.parameters, config.getDialect().getExpressionFunctions(), config.getClassHelper());
    return new NodePreparedSqlBuilder(config, SqlKind.SCRIPT, null, evaluator, SqlLogType.NONE);
  }

  private SqlStatement toSqlStatement(PreparedSql preparedSql) {
    List<SqlArgument> arguments =
        preparedSql.getParameters().stream()
            .map(
                it -> {
                  Wrapper<?> w = it.getWrapper();
                  return new SqlArgument(w.getBasicClass(), w.get());
                })
            .collect(Collectors.toList());
    String rawSql = getFormattedSql(preparedSql.getRawSql());
    String formattedSql = getFormattedSql(preparedSql.getFormattedSql());
    return new SqlStatement(rawSql, formattedSql, arguments);
  }

  private String getFormattedSql(String rawSql) {
    String formattedSql = rawSql;
    formattedSql = formattedSql.replace("\n", " ").replace("\r", " ");
    formattedSql = formattedSql.replaceAll("\\s{2,}", " ");
    return formattedSql;
  }

  @SneakyThrows
  private String getSqlTemplate(String path) {
    if (SQL_CACHE.containsKey(this.sqlFilePath)) {
      return SQL_CACHE.get(this.sqlFilePath);
    }
    URI pathUri =
        Objects.requireNonNull(this.getClass().getClassLoader().getResource(path)).toURI();
    String sqlTemplate = Files.readString(Path.of(pathUri));
    SQL_CACHE.put(this.sqlFilePath, sqlTemplate);
    return sqlTemplate;
  }
}
