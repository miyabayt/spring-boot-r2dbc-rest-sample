package com.bigtreetc.sample.r2dbc.domain.service.users;

import static org.assertj.core.api.Assertions.assertThat;

import com.bigtreetc.sample.r2dbc.BaseTestContainerTest;
import com.bigtreetc.sample.r2dbc.domain.model.user.UserCriteria;
import com.bigtreetc.sample.r2dbc.domain.service.UserService;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest extends BaseTestContainerTest {

  @Autowired UserService userService;

  @Test
  @DisplayName("ユーザの検索結果が返ること")
  void test1() {
    val criteria = new UserCriteria();
    criteria.setFirstName("john");
    criteria.setLastName("doe");
    criteria.setEmail("test@example.com");
    val found = userService.findAll(criteria, Pageable.unpaged());

    StepVerifier.create(found)
        .assertNext(
            pages -> {
              assertThat(pages).isNotNull();
              assertThat(pages.getContent()).isNotNull();
              assertThat(pages.getContent().get(0).getEmail()).isEqualTo("test@example.com");
            })
        .verifyComplete();
  }
}
