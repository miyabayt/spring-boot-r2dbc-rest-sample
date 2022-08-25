package com.bigtreetc.sample.r2dbc.base.domain.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.sql.DataSource;
import lombok.val;
import org.seasar.doma.internal.WrapException;
import org.seasar.doma.internal.expr.ExpressionEvaluator;
import org.seasar.doma.internal.expr.Value;
import org.seasar.doma.internal.jdbc.sql.NodePreparedSqlBuilder;
import org.seasar.doma.internal.jdbc.sql.SqlParser;
import org.seasar.doma.internal.jdbc.sql.node.ExpandNode;
import org.seasar.doma.internal.jdbc.util.SqlFileUtil;
import org.seasar.doma.internal.util.ResourceUtil;
import org.seasar.doma.jdbc.*;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.entity.EntityType;
import org.seasar.doma.message.Message;

public class DomaSqlFileSelectQueryBuilder {

  private Map<String, Value> parameters = new HashMap<>();

  private SelectOptions options = SelectOptions.get();

  private String sqlFilePath;

  private Naming naming = Naming.DEFAULT;

  private Dialect dialect;

  private EntityType<?> entityType;

  private SqlLogType sqlLogType = SqlLogType.FORMATTED;

  public static DomaSqlFileSelectQueryBuilder builder() {
    return new DomaSqlFileSelectQueryBuilder();
  }

  public DomaSqlFileSelectQueryBuilder options(SelectOptions options) {
    this.options = options;
    return this;
  }

  public DomaSqlFileSelectQueryBuilder sqlFilePath(String sqlFilePath) {
    this.sqlFilePath = sqlFilePath;
    return this;
  }

  public DomaSqlFileSelectQueryBuilder dialect(Dialect dialect) {
    this.dialect = dialect;
    return this;
  }

  public DomaSqlFileSelectQueryBuilder addParameter(String name, Class<?> type, Object value) {
    parameters.put(name, new Value(type, value));
    return this;
  }

  public String build() {
    String primaryPath = getPrimaryPath(sqlFilePath, dialect);
    String sql = getSql(primaryPath);
    SqlFile sqlFile = null;
    if (sql != null) {
      SqlNode sqlNode = parse(sql);
      sqlFile = new SqlFile(primaryPath, sql, sqlNode);
    }
    sql = getSql(sqlFilePath);
    if (sql != null) {
      SqlNode sqlNode = parse(sql);
      sqlFile = new SqlFile(sqlFilePath, sql, sqlNode);
    }
    if (sqlFile == null) {
      throw new SqlFileNotFoundException(sqlFilePath);
    }

    SqlNode transformedSqlNode = dialect.transformSelectSqlNode(sqlFile.getSqlNode(), options);
    return buildSql(
            (evaluator, expander) -> {
              val sqlBuilder =
                  new NodePreparedSqlBuilder(
                      new Config() {
                        @Override
                        public DataSource getDataSource() {
                          throw new UnsupportedOperationException();
                        }

                        @Override
                        public Dialect getDialect() {
                          return dialect;
                        }
                      },
                      SqlKind.SELECT,
                      sqlFilePath,
                      evaluator,
                      sqlLogType,
                      expander);
              return sqlBuilder.build(transformedSqlNode, this::comment);
            })
        .getFormattedSql();
  }

  private String comment(String sql) {
    return ConfigSupport.defaultCommenter.comment(sql, null);
  }

  private PreparedSql buildSql(
      BiFunction<ExpressionEvaluator, Function<ExpandNode, List<String>>, PreparedSql> sqlBuilder) {
    ExpressionEvaluator evaluator =
        new ExpressionEvaluator(
            parameters, dialect.getExpressionFunctions(), ConfigSupport.defaultClassHelper);
    return sqlBuilder.apply(evaluator, this::expandColumns);
  }

  private List<String> expandColumns(ExpandNode node) {
    return List.of("*");
  }

  private String getSql(String path) {
    try {
      return ResourceUtil.getResourceAsString(path);
    } catch (WrapException e) {
      Throwable cause = e.getCause();
      throw new JdbcException(Message.DOMA2010, cause, path, cause);
    }
  }

  private final String getPrimaryPath(String path, Dialect dialect) {
    return SqlFileUtil.convertToDbmsSpecificPath(path, dialect);
  }

  private final SqlNode parse(String sql) {
    SqlParser parser = new SqlParser(sql);
    return parser.parse();
  }
}
