package com.bigtreetc.sample.r2dbc.domain.repository;

import com.bigtreetc.sample.r2dbc.domain.model.MailTemplate;
import com.bigtreetc.sample.r2dbc.domain.model.MailTemplateCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MailTemplateQueryRepository {

  Mono<MailTemplate> findOne(final MailTemplateCriteria criteria);

  Mono<Page<MailTemplate>> findAll(final MailTemplateCriteria criteria, final Pageable pageable);

  Flux<MailTemplate> findAll(final MailTemplateCriteria criteria);
}
