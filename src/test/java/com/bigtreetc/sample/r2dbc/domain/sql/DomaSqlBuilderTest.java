package com.bigtreetc.sample.r2dbc.domain.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaSqlBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.seasar.doma.jdbc.dialect.MysqlDialect;

class DomaSqlBuilderTest {

  @Test
  @DisplayName("単純なSQL文の作成ができること")
  void test01() {
    val criteria = new StaffCriteria();
    criteria.setName("john");
    criteria.setEmail("test@example.com");

    val sql =
        DomaSqlBuilder.builder()
            .dialect(new MysqlDialect())
            .sqlFilePath("META-INF/com/bigtreetc/sample/r2dbc/domain/sql/select.sql")
            .addParameter("criteria", StaffCriteria.class, criteria)
            .build();

    assertThat(sql).contains("'john'");
    assertThat(sql).contains("'test@example.com'");
  }
}

@Setter
@Getter
class StaffCriteria {

  String name;

  String email;
}
