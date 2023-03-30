package com.bigtreetc.sample.r2dbc;

import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaDatabaseClient;
import com.bigtreetc.sample.r2dbc.base.domain.sql.DomaDatabaseClientImpl;
import io.r2dbc.spi.ConnectionFactory;
import java.util.List;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.MysqlDialect;
import org.seasar.doma.jdbc.dialect.PostgresDialect;
import org.seasar.doma.jdbc.dialect.StandardDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class R2dbcConfig extends AbstractR2dbcConfiguration {

  @Autowired ConnectionFactory connectionFactory;

  @Override
  public ConnectionFactory connectionFactory() {
    return connectionFactory;
  }

  @WritingConverter
  public static class UUIDToStringConverter implements Converter<UUID, String> {
    @Override
    public String convert(UUID source) {
      return source.toString();
    }
  }

  @ReadingConverter
  public static class StringToUUIDConverter implements Converter<String, UUID> {
    @Override
    public UUID convert(String source) {
      return UUID.fromString(source);
    }
  }

  @Override
  protected List<Object> getCustomConverters() {
    return List.of(new UUIDToStringConverter(), new StringToUUIDConverter());
  }

  @Bean
  public ReactiveAuditorAware<String> auditorAware() {
    return () ->
        ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
  }

  @Bean(initMethod = "migrate")
  public Flyway flyway(FlywayProperties flywayProperties) {
    return new Flyway(
        Flyway.configure()
            .baselineOnMigrate(flywayProperties.isBaselineOnMigrate())
            .placeholderReplacement(flywayProperties.isPlaceholderReplacement())
            .schemas(StringUtils.toStringArray(flywayProperties.getSchemas()))
            .dataSource(
                flywayProperties.getUrl(),
                flywayProperties.getUser(),
                flywayProperties.getPassword()));
  }

  @Bean
  public Dialect dialect(ConnectionFactory connectionFactory) {
    String dialectName = DialectResolver.getDialect(connectionFactory).getClass().getSimpleName();
    switch (dialectName) {
      case "MySqlDialect" -> {
        return new MysqlDialect();
      }
      case "PostgresDialect" -> {
        return new PostgresDialect();
      }
    }
    return new StandardDialect();
  }

  @Bean
  public DomaDatabaseClient domaDatabaseClient(
      R2dbcEntityTemplate r2dbcEntityTemplate, MappingR2dbcConverter converter, Dialect dialect) {
    return new DomaDatabaseClientImpl(r2dbcEntityTemplate, converter, dialect);
  }
}
