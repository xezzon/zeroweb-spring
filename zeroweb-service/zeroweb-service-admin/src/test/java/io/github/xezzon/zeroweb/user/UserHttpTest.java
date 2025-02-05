package io.github.xezzon.zeroweb.user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.InitializeDataRunner;
import io.github.xezzon.zeroweb.common.constant.CharacterConstant;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.exception.CommonErrorCode;
import io.github.xezzon.zeroweb.user.domain.User;
import io.github.xezzon.zeroweb.user.entity.RegisterUserReq;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class UserHttpTest {

  private static final String USER_REGISTER_URI = "/user/register";

  @Resource
  private UserRepository repository;
  @Resource
  private WebTestClient webTestClient;
  @Resource
  private InitializeDataRunner dataset;

  @Test
  @Transactional
  void addUser() {
    RegisterUserReq req = new RegisterUserReq();
    req.setUsername(RandomUtil.randomString(9));
    req.setNickname(RandomUtil.randomString(9));
    req.setPassword(
        RandomUtil.randomString(String.valueOf(CharacterConstant.getLowercase()), 4)
            + RandomUtil.randomString(String.valueOf(CharacterConstant.getUppercase()), 4)
            + RandomUtil.randomString(String.valueOf(CharacterConstant.getDigit()), 4)
    );
    Id responseBody = webTestClient.post()
        .uri(USER_REGISTER_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(io.github.xezzon.zeroweb.common.domain.Id.class)
        .returnResult()
        .getResponseBody();
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
    Optional<User> after = repository.findById(responseBody.id());
    assertTrue(after.isPresent());
  }

  @Test
  void addUser_repeat() {
    // 数据准备
    User data = dataset.getUsers().get(0);

    RegisterUserReq req = new RegisterUserReq();
    req.setUsername(data.getUsername());
    req.setNickname(RandomUtil.randomString(8));
    req.setPassword(
        RandomUtil.randomString(String.valueOf(CharacterConstant.getLowercase()), 4)
            + RandomUtil.randomString(String.valueOf(CharacterConstant.getUppercase()), 4)
            + RandomUtil.randomString(String.valueOf(CharacterConstant.getDigit()), 4)
    );
    webTestClient.post()
        .uri(USER_REGISTER_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(CommonErrorCode.REPEAT_DATA.code());
  }
}
