package com.bigtreetc.sample.r2dbc.domain.repository;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.domain.model.system.Holiday;
import com.bigtreetc.sample.r2dbc.domain.model.system.HolidayCriteria;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
public class HolidayQueryRepositoryImpl implements HolidayQueryRepository {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  /**
   * 指定された条件で祝日マスタを検索します。
   *
   * @param example
   * @param pageable
   * @return
   */
  public Mono<Page<Holiday>> findAll(
      final Example<HolidayCriteria> example, final Pageable pageable) {
    val criteria = getCriteria(example);
    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(Holiday.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(r2dbcEntityTemplate.count(query, Holiday.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  private static List<Criteria> getCriteria(Example<HolidayCriteria> example) {
    val holiday = example.getProbe();
    val criteria = new ArrayList<Criteria>();

    if (holiday.getHolidayDate() != null) {
      criteria.add(
          where("holiday_date").between(holiday.getHolidayDateFrom(), holiday.getHolidayDateTo()));
    }
    if (isNotEmpty(holiday.getHolidayName())) {
      criteria.add(where("holiday_name").like("%%%s%%".formatted(holiday.getHolidayName())));
    }

    return criteria;
  }
}
