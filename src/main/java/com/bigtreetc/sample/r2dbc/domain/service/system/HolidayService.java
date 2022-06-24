package com.bigtreetc.sample.r2dbc.domain.service.system;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.base.exception.NoDataFoundException;
import com.bigtreetc.sample.r2dbc.domain.model.system.Holiday;
import com.bigtreetc.sample.r2dbc.domain.model.system.HolidayCriteria;
import com.bigtreetc.sample.r2dbc.domain.repository.system.HolidayRepository;
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

/** 祝日サービス */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
public class HolidayService {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  @NonNull final HolidayRepository holidayRepository;

  /**
   * 祝日を検索します。
   *
   * @param holiday
   * @param pageable
   * @return
   */
  @Transactional(readOnly = true) // 読み取りのみの場合は指定する
  public Mono<Page<Holiday>> findAll(HolidayCriteria holiday, Pageable pageable) {
    Assert.notNull(holiday, "holiday must not be null");

    val criteria = new ArrayList<Criteria>();
    if (holiday.getHolidayDate() != null) {
      criteria.add(
          where("holiday_date").between(holiday.getHolidayDateFrom(), holiday.getHolidayDateTo()));
    }
    if (isNotEmpty(holiday.getHolidayName())) {
      criteria.add(where("holiday_name").like("%%%s%%".formatted(holiday.getHolidayName())));
    }

    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(Holiday.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(r2dbcEntityTemplate.count(query, Holiday.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  /**
   * 祝日を取得します。
   *
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Holiday> findOne(HolidayCriteria holiday) {
    Assert.notNull(holiday, "holiday must not be null");
    return holidayRepository.findOne(Example.of(holiday));
  }

  /**
   * 祝日を取得します。
   *
   * @return
   */
  @Transactional(readOnly = true)
  public Mono<Holiday> findById(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return holidayRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new NoDataFoundException("id=" + id + " のデータが見つかりません。")));
  }

  /**
   * 祝日を登録します。
   *
   * @param holiday
   * @return
   */
  public Mono<Holiday> create(final Holiday holiday) {
    Assert.notNull(holiday, "holiday must not be null");
    holiday.setId(UUID.randomUUID());
    return holidayRepository.save(holiday);
  }

  /**
   * 祝日を登録します。
   *
   * @param holidays
   * @return
   */
  public Flux<Holiday> create(final List<Holiday> holidays) {
    Assert.notNull(holidays, "holidays must not be null");
    for (val holiday : holidays) {
      holiday.setId(UUID.randomUUID());
    }
    return holidayRepository.saveAll(holidays);
  }

  /**
   * 祝日を更新します。
   *
   * @param holiday
   * @return
   */
  public Mono<Holiday> update(final Holiday holiday) {
    Assert.notNull(holiday, "holiday must not be null");
    return holidayRepository.save(holiday);
  }

  /**
   * 祝日を更新します。
   *
   * @param holidays
   * @return
   */
  public Flux<Holiday> update(final List<Holiday> holidays) {
    Assert.notNull(holidays, "holiday must not be null");
    return holidayRepository.saveAll(holidays);
  }

  /**
   * 祝日を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final UUID id) {
    Assert.notNull(id, "id must not be null");
    return holidayRepository.deleteById(id);
  }

  /**
   * 祝日を削除します。
   *
   * @return
   */
  public Mono<Void> delete(final List<UUID> ids) {
    Assert.notNull(ids, "id must not be null");
    return holidayRepository.deleteAllById(ids);
  }
}
