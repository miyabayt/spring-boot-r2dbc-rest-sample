package com.bigtreetc.sample.r2dbc.domain.model;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("upload_files")
public class UploadFile extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  @Id UUID id;

  // ファイル名
  @Column("file_name")
  String filename;

  // オリジナルファイル名
  @Column("original_file_name")
  String originalFilename;

  // コンテンツタイプ
  String contentType;

  // コンテンツ
  byte[] content;

  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }
}
