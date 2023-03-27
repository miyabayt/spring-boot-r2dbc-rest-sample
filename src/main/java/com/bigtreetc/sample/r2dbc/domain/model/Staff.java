package com.bigtreetc.sample.r2dbc.domain.model;

import com.bigtreetc.sample.r2dbc.base.domain.model.BaseEntityImpl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("staffs")
public class Staff extends BaseEntityImpl implements Persistable<UUID> {

  private static final long serialVersionUID = -1L;

  @Id UUID id;

  @JsonIgnore String password;

  // 名
  String firstName;

  // 姓
  String lastName;

  // 氏名
  @Transient String fullName;

  // メールアドレス
  @Email String email;

  // 電話番号
  @Digits(fraction = 0, integer = 10)
  String tel;

  // パスワードリセットトークン
  @JsonIgnore String passwordResetToken;

  // トークン失効日
  @JsonIgnore LocalDateTime tokenExpiresAt;

  @JsonIgnore
  @Override
  public boolean isNew() {
    return getCreatedAt() == null;
  }
}
