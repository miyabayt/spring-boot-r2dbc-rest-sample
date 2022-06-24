package com.bigtreetc.sample.r2dbc.base.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BaseEntity {

  UUID getId();

  String getCreatedBy();

  void setCreatedBy(String createdBy);

  LocalDateTime getCreatedAt();

  void setCreatedAt(LocalDateTime createdAt);

  String getUpdatedBy();

  void setUpdatedBy(String updatedBy);

  LocalDateTime getUpdatedAt();

  void setUpdatedAt(LocalDateTime updatedAt);

  Integer getVersion();

  void setVersion(Integer version);
}
