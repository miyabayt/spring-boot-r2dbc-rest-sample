package com.bigtreetc.sample.r2dbc.domain.model;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("codes")
public class Code extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  // コード定義ID
  @Id UUID id;

  // 分類コード
  String categoryCode;

  // 分類名
  @Transient String categoryName;

  // コード値
  String codeValue;

  // コード名
  String codeName;

  // エイリアス
  String codeAlias;

  // 表示順
  Integer displayOrder;

  // 無効フラグ
  Boolean isInvalid;

  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }
}
