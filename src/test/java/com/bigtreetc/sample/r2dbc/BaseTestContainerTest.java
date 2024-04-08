package com.bigtreetc.sample.r2dbc;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class BaseTestContainerTest {

  static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.2");

  static final GenericContainer<?> MAILHOG_CONTAINER =
      new GenericContainer<>("mailhog/mailhog")
          .withExposedPorts(1025, 8025)
          .waitingFor(Wait.forHttp("/").forPort(8025));

  static {
    MYSQL_CONTAINER.start();
    MAILHOG_CONTAINER.start();
  }

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.r2dbc.url",
        () ->
            "r2dbc:mysql://%s:%d/%s"
                .formatted(
                    MYSQL_CONTAINER.getHost(),
                    MYSQL_CONTAINER.getMappedPort(MySQLContainer.MYSQL_PORT),
                    MYSQL_CONTAINER.getDatabaseName()));
    registry.add("spring.r2dbc.username", MYSQL_CONTAINER::getUsername);
    registry.add("spring.r2dbc.password", MYSQL_CONTAINER::getPassword);
    registry.add("spring.flyway.url", MYSQL_CONTAINER::getJdbcUrl);
    registry.add("spring.flyway.user", MYSQL_CONTAINER::getUsername);
    registry.add("spring.flyway.password", MYSQL_CONTAINER::getPassword);
    registry.add("spring.mail.host", MAILHOG_CONTAINER::getHost);
    registry.add("spring.mail.port", MAILHOG_CONTAINER::getFirstMappedPort);
  }
}
