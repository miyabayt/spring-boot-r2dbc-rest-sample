package com.bigtreetc.sample.r2dbc.controller.system.codecategories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

import com.bigtreetc.sample.r2dbc.BaseTestContainerTest;
import com.bigtreetc.sample.r2dbc.base.web.security.jwt.JwtRepository;
import java.util.Collections;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class CodeCategoryControllerTest extends BaseTestContainerTest {

  @Autowired ApplicationContext context;

  @Autowired JwtRepository jwtRepository;

  WebTestClient webClient;

  @BeforeEach
  void setup() {
    webClient =
        WebTestClient.bindToApplicationContext(context)
            .apply(springSecurity())
            .configureClient()
            .build();
  }

  @Test
  @DisplayName("権限を持つロールでデータが取得できること")
  void test1() throws Exception {
    val token = jwtRepository.createAccessToken("test", List.of("codeCategory:read")).block();
    assertThat(token).isNotNull();

    webClient
        .get()
        .uri("/api/system/codeCategories")
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("権限を持たないロールではエラーになること")
  void test2() throws Exception {
    val token = jwtRepository.createAccessToken("test", Collections.emptyList()).block();
    assertThat(token).isNotNull();

    webClient
        .get()
        .uri("/api/system/codeCategories")
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isForbidden();
  }
}
