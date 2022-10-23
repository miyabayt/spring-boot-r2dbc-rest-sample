package com.bigtreetc.sample.r2dbc.domain.model.user;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import com.bigtreetc.sample.r2dbc.domain.model.system.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("users")
public class User extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  @Id UUID id;

  // ハッシュ化されたパスワード
  @JsonIgnore String password;

  // 名
  String firstName;

  // 姓
  String lastName;

  // メールアドレス
  @Email String email;

  // 電話番号
  @Digits(fraction = 0, integer = 10)
  String tel;

  // 郵便番号
  @NotEmpty String zip;

  // 住所
  @NotEmpty String address;

  // 添付ファイルID
  @JsonIgnore Long uploadFileId;

  // 添付ファイル
  @Transient @JsonIgnore UploadFile uploadFile;

  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }
}
