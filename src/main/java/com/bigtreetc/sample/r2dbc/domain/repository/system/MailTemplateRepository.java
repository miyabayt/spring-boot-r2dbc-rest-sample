package com.bigtreetc.sample.r2dbc.domain.repository.system;

import com.bigtreetc.sample.r2dbc.domain.model.system.MailTemplate;
import java.util.UUID;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/** メールテンプレートリポジトリ */
@Repository
public interface MailTemplateRepository
    extends ReactiveSortingRepository<MailTemplate, UUID>,
        ReactiveQueryByExampleExecutor<MailTemplate> {

  Mono<MailTemplate> findByTemplateCode(String templateCode);
}
