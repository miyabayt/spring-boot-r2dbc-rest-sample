package com.bigtreetc.sample.r2dbc.domain.repository;

import static com.bigtreetc.sample.r2dbc.base.util.ValidateUtils.isNotEmpty;
import static org.springframework.data.relational.core.query.Criteria.where;

import com.bigtreetc.sample.r2dbc.domain.model.Staff;
import com.bigtreetc.sample.r2dbc.domain.model.StaffCriteria;
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
public class StaffQueryRepositoryImpl implements StaffQueryRepository {

  @NonNull final R2dbcEntityTemplate r2dbcEntityTemplate;

  /**
   * 指定された条件で担当者マスタを検索します。
   *
   * @param example
   * @param pageable
   * @return
   */
  public Mono<Page<Staff>> findAll(final Example<StaffCriteria> example, final Pageable pageable) {
    val criteria = getCriteria(example);
    val query = Query.query(Criteria.from(criteria));
    return r2dbcEntityTemplate
        .select(Staff.class)
        .matching(query.with(pageable))
        .all()
        .collectList()
        .zipWith(r2dbcEntityTemplate.count(query, Staff.class))
        .map(tuple2 -> new PageImpl<>(tuple2.getT1(), pageable, tuple2.getT2()));
  }

  private static List<Criteria> getCriteria(Example<StaffCriteria> example) {
    val staff = example.getProbe();
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

    return criteria;
  }
}
