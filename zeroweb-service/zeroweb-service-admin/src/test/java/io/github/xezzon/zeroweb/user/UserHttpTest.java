package io.github.xezzon.zeroweb.user;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.constant.CharacterConstant;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.user.constant.UserConstant;
import io.github.xezzon.zeroweb.user.entity.RegisterUserReq;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserHttpTest {

  private static final String USER_REGISTER_URI = "/user/register";
  private static final String USER_LIST_URI = "/user";

  private final User user = new User();
  @Resource
  private UserRepository repository;
  @Resource
  private RestTestClient testClient;

  @BeforeEach
  void setUp() {
    user.setUsername(RandomUtil.randomString(8));
    user.setNickname(RandomUtil.randomString(8));
    user.setCipher(BCrypt.hashpw(RandomUtil.randomString(8)));
    repository.save(user);
  }

  @AfterEach
  void tearDown() {
    List<User> users = repository.findAll();
    users = users.stream()
        .filter(o -> !Objects.equals(o.getId(), UserConstant.ROOT.getId()))
        .toList();
    repository.deleteAll(users);
  }

  @Test
  void addUser() {
    RegisterUserReq req = new RegisterUserReq(
        RandomUtil.randomString(9),
        RandomUtil.randomString(9),
        RandomUtil.randomString(String.valueOf(CharacterConstant.getLowercase()), 4)
            + RandomUtil.randomString(String.valueOf(CharacterConstant.getUppercase()), 4)
            + RandomUtil.randomString(String.valueOf(CharacterConstant.getDigit()), 4)
    );
    Id responseBody = testClient.post()
        .uri(USER_REGISTER_URI)
        .body(req)
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
    testClient.post()
        .uri(USER_REGISTER_URI)
        .body(req)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, RepeatDataException.ERROR_CODE);
  }

  @Test
  void getUserPaged() {
    final int top = 5;
    final int skip = 0;
    PagedModel<User> responseBody = testClient.get()
        .uri(builder -> builder
            .path(USER_LIST_URI)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<PagedModel<User>>() {
        })
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertEquals(2, responseBody.getPage().getTotalElements());
    assertEquals(2, responseBody.getContent().size());
    assertTrue(responseBody.getContent().stream().anyMatch(
        o -> Objects.equals(o.getId(), user.getId())
    ));
    assertTrue(responseBody.getContent().stream().anyMatch(
        o -> Objects.equals(o.getId(), UserConstant.ROOT.getId())
    ));
  }
}
