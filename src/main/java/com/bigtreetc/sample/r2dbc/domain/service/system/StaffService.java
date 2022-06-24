package com.bigtreetc.sample.r2dbc.domain.service.system;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.system.Staff;
import com.bigtreetc.sample.r2dbc.domain.model.system.StaffCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.system.StaffRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 担当者サービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class StaffService {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final StaffRepository staffRepository;

  /**
   * 担当者を検索します。
   *
   * @param staff
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<Staff>> findAll(final StaffCriteria staff, final Pageable pageable) {
    Assert.notNull(staff, "staff must not be null");

    val criteria = new ArrayList<Criteria>();
    if (isNotEmpty(staff.getFirstName())) {
      criteria.add(where("first_name").like("%%%s%%".formatted(staff.getFirstName())));
    }
    if (isNotEmpty(staff.getLastName())) {
      criteria.add(where("last_name").like("%%%s%%".formatted(staff.getLastName())));
    }
    if (isNotEmpty(staff.getEmail())) {
      criteria.add(where("email").like("%%%s%%".formatted(staff.getEmail())));
    }

    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(Staff.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(r2dbcEntityTemplate.count(query, Staff.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  /**
   * 担当者を取得します。
   *
   * @param staff
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Staff> findOne(StaffCriteria staff) {
    Assert.notNull(staff, "staff must not be null");
    return staffRepository.findOne(Example.of(staff));
  }

  /**
   * 担当者を取得します。
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
   * 担当者を登録します。
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
   * 担当者を登録します。
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
   * 担当者を更新します。
   *
   * @param staff
   * @return
   */
  public Mono<Staff> update(final Staff staff) {
    Assert.notNull(staff, "staff must not be null");
    return staffRepository.save(staff);
  }

  /**
   * 担当者を更新します。
   *
   * @param staffs
   * @return
   */
  public Flux<Staff> update(final List<Staff> staffs) {
    Assert.notNull(staffs, "staff must not be null");
    return staffRepository.saveAll(staffs);
  }

  /**
   * 担当者を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return staffRepository.deleteById(id);
  }

  /**
   * 担当者を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return staffRepository.deleteAllById(ids);
  }
}
