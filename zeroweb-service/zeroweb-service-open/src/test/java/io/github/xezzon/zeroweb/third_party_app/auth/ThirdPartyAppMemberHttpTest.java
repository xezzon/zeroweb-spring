package io.github.xezzon.zeroweb.third_party_app.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMemberRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import jakarta.annotation.Resource;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class ThirdPartyAppMemberHttpTest {

  private static final String INVITE_MEMBER = "/third-party-app/{appId}/member";
  private static final String ACCEPT_INVITATION = "/third-party-app/-/member";
  private static final String OWNER_ID = UUID.randomUUID().toString();

  @Resource
  private ThirdPartyAppRepository thirdPartyAppRepository;
  @Resource
  private AccessSecretRepository accessSecretRepository;
  @Resource
  private ThirdPartyAppMemberRepository thirdPartyAppMemberRepository;
  @Resource
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
    thirdPartyApp.setName(RandomUtil.randomString(8));
    thirdPartyApp.setOwnerId(OWNER_ID);
    thirdPartyAppRepository.save(thirdPartyApp);
    accessSecretRepository
        .updateSecretKeyById(thirdPartyApp.getId(), RandomUtil.randomString(8));
  }

  @AfterEach
  void tearDown() {
    thirdPartyAppRepository.deleteAll();
  }

  @Test
  void addMember_general() {
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    String token = webTestClient.post()
        .uri(builder -> builder
            .path(INVITE_MEMBER)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(OWNER_ID).bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult().getResponseBody();

    Id memberId = webTestClient.put()
        .uri(builder -> builder
            .path(ACCEPT_INVITATION)
            .queryParam("token", token)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .id(UUID.randomUUID().toString())
            .bearer()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(memberId);
    Assertions.assertTrue(thirdPartyAppMemberRepository.existsById(memberId.id()));
  }

  @Test
  void addMember_particular() {
    String invitedUser = UUID.randomUUID().toString();
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    String token = webTestClient.post()
        .uri(builder -> builder
            .path(INVITE_MEMBER)
            .queryParam("userId", invitedUser)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(OWNER_ID).bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult().getResponseBody();

    Id memberId = webTestClient.put()
        .uri(builder -> builder
            .path(ACCEPT_INVITATION)
            .queryParam("token", token)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(invitedUser).bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(memberId);
    Assertions.assertTrue(thirdPartyAppMemberRepository.existsById(memberId.id()));

    webTestClient.put()
        .uri(builder -> builder
            .path(ACCEPT_INVITATION)
            .queryParam("token", token)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .id(UUID.randomUUID().toString())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void addMember_timeout() {
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    String token = webTestClient.post()
        .uri(builder -> builder
            .path(INVITE_MEMBER)
            .queryParam("timeout", 0)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(OWNER_ID).bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .returnResult().getResponseBody();

    webTestClient.put()
        .uri(builder -> builder
            .path(ACCEPT_INVITATION)
            .queryParam("token", token)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .id(UUID.randomUUID().toString())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden();
  }
}
