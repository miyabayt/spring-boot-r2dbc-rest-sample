package com.bigtreetc.sample.r2dbc.controller.mailtemplates;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

import com.bigtreetc.sample.r2dbc.BaseTestContainerTest;
import com.bigtreetc.sample.r2dbc.base.web.security.jwt.JwtRepository;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class MailTemplateControllerTest extends BaseTestContainerTest {

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
  @DisplayName("権限を持つロールで、メールテンプレートを検索できること")
  void test1() throws Exception {
    val token = jwtRepository.createAccessToken("test", List.of("mailTemplate:read")).block();
    assertThat(token).isNotNull();

    webClient
        .get()
        .uri("/api/system/mailTemplates")
        .accept(MediaType.APPLICATION_JSON)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.success")
        .isEqualTo("true")
        .jsonPath("$.message")
        .isEqualTo("正常終了");
  }

  @Test
  @DisplayName("権限を持たないロールでは、メールテンプレート検索がエラーになること")
  void test2() throws Exception {
    val token = jwtRepository.createAccessToken("test", emptyList()).block();
    assertThat(token).isNotNull();

    webClient
        .get()
        .uri("/api/system/mailTemplates")
        .accept(MediaType.APPLICATION_JSON)
        .headers(http -> http.setBearerAuth(token))
        .exchange()
        .expectStatus()
        .isForbidden()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.success")
        .isEqualTo("false")
        .jsonPath("$.message")
        .isEqualTo("権限がありません。");
  }
}
