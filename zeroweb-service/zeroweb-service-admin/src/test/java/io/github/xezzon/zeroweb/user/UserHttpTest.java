package io.github.xezzon.zeroweb.user;

import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.constant.CharacterConstant;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.user.entity.RegisterUserReq;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class UserHttpTest {

  private static final String USER_REGISTER_URI = "/user/register";

  private final User user = new User();
  @Resource
  private UserRepository repository;
  @Resource
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    user.setUsername(RandomUtil.randomString(8));
    user.setNickname(RandomUtil.randomString(8));
    user.setCipher(BCrypt.hashpw(RandomUtil.randomString(8)));
    repository.save(user);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  @Transactional
  void addUser() {
    RegisterUserReq req = new RegisterUserReq(
        RandomUtil.randomString(9),
        RandomUtil.randomString(9),
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
    RegisterUserReq req = new RegisterUserReq(
        user.getUsername(),
        RandomUtil.randomString(8),
        RandomUtil.randomString(String.valueOf(CharacterConstant.getLowercase()), 4)
            + RandomUtil.randomString(String.valueOf(CharacterConstant.getUppercase()), 4)
            + RandomUtil.randomString(String.valueOf(CharacterConstant.getDigit()), 4)
    );
    webTestClient.post()
        .uri(USER_REGISTER_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, RepeatDataException.ERROR_CODE);
  }
}
