package com.bigtreetc.sample.r2dbc.base.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;

@Getter
@Setter
@SuppressWarnings("serial")
public abstract class BaseEntityImpl implements BaseEntity, Serializable {

  // 作成者
  @JsonIgnore @CreatedBy String createdBy;

  // 作成日時
  @CreatedDate LocalDateTime createdAt;

  // 更新者
  @JsonIgnore @LastModifiedBy String updatedBy;

  // 更新日時
  @LastModifiedDate LocalDateTime updatedAt;

  // 楽観的排他制御で使用する改定番号
  @Version Integer version;
}
