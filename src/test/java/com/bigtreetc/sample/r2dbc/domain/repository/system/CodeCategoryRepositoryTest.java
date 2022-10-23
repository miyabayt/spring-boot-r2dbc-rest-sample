package com.bigtreetc.sample.r2dbc.domain.repository.system;

import static org.assertj.core.api.Assertions.assertThat;

import com.bigtreetc.sample.r2dbc.BaseTestContainerTest;
import com.bigtreetc.sample.r2dbc.domain.model.system.CodeCategory;
import com.bigtreetc.sample.r2dbc.domain.repository.CodeCategoryRepository;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeCategoryRepositoryTest extends BaseTestContainerTest {

  @Autowired CodeCategoryRepository codeCategoryRepository;

  @Test
  @DisplayName("リポジトリがNULLではないこと")
  void test1() {
    assertThat(codeCategoryRepository).isNotNull();
  }

  @Test
  @DisplayName("指定した分類コードのレコードが取得できること")
  void test2() {
    val expectedCategoryCode = "target";
    val codeCategory = new CodeCategory();
    codeCategory.setCategoryCode(expectedCategoryCode);

    val example = Example.of(codeCategory);
    val found = codeCategoryRepository.findOne(example);

    StepVerifier.create(found)
        .assertNext(
            cc -> {
              assertThat(cc).isNotNull();
              assertThat(cc.getCategoryCode()).isEqualTo(expectedCategoryCode);
            })
        .verifyComplete();
  }
}
