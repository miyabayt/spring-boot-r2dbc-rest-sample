package com.bigtreetc.sample.r2dbc.domain.model.system;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("code_categories")
public class CodeCategory extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  // コード分類ID
  @Id UUID id;

  // カテゴリコード
  String categoryCode;

  // カテゴリ名
  String categoryName;

  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }
}
