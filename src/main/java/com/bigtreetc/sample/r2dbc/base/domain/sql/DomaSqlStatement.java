package com.bigtreetc.sample.r2dbc.base.domain.sql;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.seasar.doma.jdbc.PreparedSql;
import org.seasar.doma.template.SqlArgument;
import org.seasar.doma.template.SqlStatement;

@RequiredArgsConstructor
public class DomaSqlStatement {

  @NonNull final SqlStatement sqlStatement;

  @NonNull final PreparedSql countPreparedSql;

  public String getRawSql() {
    return sqlStatement.getRawSql();
  }

  public String getFormattedSql() {
    return sqlStatement.getFormattedSql();
  }

  public List<SqlArgument> getArguments() {
    return sqlStatement.getArguments();
  }

  public String getCountRawSql() {
    return countPreparedSql.getRawSql();
  }

  public String getCountFormattedSql() {
    return countPreparedSql.getFormattedSql();
  }
}
