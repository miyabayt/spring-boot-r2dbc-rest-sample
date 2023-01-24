package com.bigtreetc.sample.r2dbc.base.domain.sql;

import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.data.domain.Pageable;

/** Doma関連ユーティリティ */
public class DomaUtils {

  /**
   * SelectOptionsを作成して返します。
   *
   * @return
   */
  public static SelectOptions toSelectOptions() {
    return SelectOptions.get();
  }

  /**
   * SearchOptionsを作成して返します。
   *
   * @param pageable
   * @return
   */
  public static SelectOptions toSelectOptions(Pageable pageable) {
    if (pageable.isUnpaged()) {
      return SelectOptions.get().count();
    }
    int page = pageable.getPageNumber();
    int perpage = pageable.getPageSize();
    return toSelectOptions(page, perpage).count();
  }

  /**
   * SearchOptionsを作成して返します。
   *
   * @param page
   * @param perpage
   * @return
   */
  public static SelectOptions toSelectOptions(int page, int perpage) {
    int offset = page * perpage;
    return SelectOptions.get().count().offset(offset).limit(perpage);
  }
}
