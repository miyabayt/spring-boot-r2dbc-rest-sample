package com.bigtreetc.sample.r2dbc.domain.service;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.Staff;
import com.bigtreetc.sample.r2dbc.domain.model.StaffCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.StaffRepository;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 担当者マスタサービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class StaffService {

  @NonNull final StaffRepository staffRepository;

  /**
   * 担当者マスタを検索します。
   *
   * @param criteria
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<Staff>> findAll(final StaffCriteria criteria, final Pageable pageable) {
    Assert.notNull(criteria, "criteria must not be null");
    Assert.notNull(pageable, "pageable must not be null");
    return staffRepository.findAll(criteria, pageable);
  }

  /**
   * 担当者マスタを検索します。
   *
   * @param criteria
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Flux<Staff> findAll(final StaffCriteria criteria) {
    Assert.notNull(criteria, "criteria must not be null");
    return staffRepository.findAll(criteria);
  }

  /**
   * 担当者マスタを取得します。
   *
   * @param criteria
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Staff> findOne(StaffCriteria criteria) {
    Assert.notNull(criteria, "criteria must not be null");
    return staffRepository.findOne(criteria);
  }

  /**
   * 担当者マスタを取得します。
   *
   * @param id
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Staff> findById(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return staffRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new NoDataFoundException("id=" + id + " のデータが見つかりません。")));
  }

  /**
   * 担当者マスタを登録します。
   *
   * @param staff
   * @return
   */
  public Mono<Staff> create(final Staff staff) {
    Assert.notNull(staff, "staff must not be null");
    staff.setId(UUID.randomUUID());
    return staffRepository.save(staff);
  }

  /**
   * 担当者マスタを登録します。
   *
   * @param staffs
   * @return
   */
  public Flux<Staff> create(final List<Staff> staffs) {
    Assert.notNull(staffs, "staffs must not be null");
    for (val staff : staffs) {
      staff.setId(UUID.randomUUID());
    }
    return staffRepository.saveAll(staffs);
  }

  /**
   * 担当者マスタを更新します。
   *
   * @param staff
   * @return
   */
  public Mono<Staff> update(final Staff staff) {
    Assert.notNull(staff, "staff must not be null");
    return staffRepository.save(staff);
  }

  /**
   * 担当者マスタを更新します。
   *
   * @param staffs
   * @return
   */
  public Flux<Staff> update(final List<Staff> staffs) {
    Assert.notNull(staffs, "staff must not be null");
    return staffRepository.saveAll(staffs);
  }

  /**
   * 担当者マスタを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return staffRepository.deleteById(id);
  }

  /**
   * 担当者マスタを削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return staffRepository.deleteAllById(ids);
  }
}
