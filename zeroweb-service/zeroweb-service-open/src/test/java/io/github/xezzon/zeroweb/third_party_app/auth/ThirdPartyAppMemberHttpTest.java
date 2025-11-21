package io.github.xezzon.zeroweb.third_party_app.auth;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.authn.InvalidInvitationCodeException;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMember;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMemberRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.AccessSecretRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import io.jsonwebtoken.Jwts.SIG;
import jakarta.annotation.Resource;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class ThirdPartyAppMemberHttpTest {

  private static final String INVITE_MEMBER = "/third-party-app/{appId}/member";
  private static final String ACCEPT_INVITATION = "/third-party-app/-/member";
  private static final String LIST_MEMBER = "/third-party-app/{appId}/member";
  private static final String MOVE_OWNERSHIP = "/third-party-app/{appId}/owner";
  private static final String OWNER_ID = UUID.randomUUID().toString();
  private static final String MEMBER_ID = UUID.randomUUID().toString();

  @Resource
  private ThirdPartyAppRepository thirdPartyAppRepository;
  @Resource
  private AccessSecretRepository accessSecretRepository;
  @Resource
  private ThirdPartyAppMemberRepository thirdPartyAppMemberRepository;
  @Resource
  private RestTestClient testClient;

  @BeforeEach
  void setUp() {
    ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
    thirdPartyApp.setName(RandomUtil.randomString(8));
    thirdPartyApp.setOwnerId(OWNER_ID);
    thirdPartyAppRepository.save(thirdPartyApp);
    String secretKey = Base64.getEncoder().encodeToString(SIG.HS256.key().build().getEncoded());
    accessSecretRepository
        .updateSecretKeyById(thirdPartyApp.getId(), secretKey);

    ThirdPartyAppMember owner = new ThirdPartyAppMember();
    owner.setGroupId(thirdPartyApp.getId());
    owner.setUserId(OWNER_ID);
    owner.setRoleId(ThirdPartyAppMember.OWNER_ROLE_ID);
    thirdPartyAppMemberRepository.save(owner);
    ThirdPartyAppMember member = new ThirdPartyAppMember();
    member.setGroupId(thirdPartyApp.getId());
    member.setUserId(MEMBER_ID);
    member.setRoleId(ThirdPartyAppMember.DEFAULT_ROLE_ID);
    thirdPartyAppMemberRepository.save(member);
  }

  @AfterEach
  void tearDown() {
    thirdPartyAppRepository.deleteAll();
  }

  @Test
  void addMember_general() {
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().getFirst();

    String token = testClient.post()
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
    Assertions.assertNotNull(token);

    Id memberId = testClient.put()
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
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().getFirst();

    String token = testClient.post()
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
    Assertions.assertNotNull(token);

    Id memberId = testClient.put()
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

    testClient.put()
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
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidInvitationCodeException.ERROR_CODE);

    List<ThirdPartyAppMember> responseBody = testClient.get()
        .uri(builder -> builder
            .path(LIST_MEMBER)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(invitedUser).bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NotNull List<ThirdPartyAppMember>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(3, responseBody.size());
  }

  @Test
  void addMember_timeout() {
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().getFirst();

    String token = testClient.post()
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
    Assertions.assertNotNull(token);

    testClient.put()
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
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, InvalidInvitationCodeException.ERROR_CODE);
  }

  @Test
  void moveOwnership() {
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().getFirst();

    testClient.patch()
        .uri(builder -> builder
            .path(MOVE_OWNERSHIP)
            .queryParam("userId", MEMBER_ID)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(OWNER_ID).bearer())
        .exchange()
        .expectStatus().isOk();

    ThirdPartyAppMember oldOwner = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(thirdPartyApp.getId(), OWNER_ID)
        .orElseThrow();
    Assertions.assertFalse(oldOwner.isOwner());
    ThirdPartyAppMember newOwner = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(thirdPartyApp.getId(), MEMBER_ID)
        .orElseThrow();
    Assertions.assertTrue(newOwner.isOwner());
  }

  @Test
  void moveOwnership_notOwner() {
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().getFirst();

    testClient.patch()
        .uri(builder -> builder
            .path(MOVE_OWNERSHIP)
            .queryParam("userId", OWNER_ID)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(MEMBER_ID).bearer())
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, DataPermissionForbiddenException.ERROR_CODE);
  }

  @Test
  void moveOwnership_notMember() {
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().getFirst();

    testClient.patch()
        .uri(builder -> builder
            .path(MOVE_OWNERSHIP)
            .queryParam("userId", UUID.randomUUID().toString())
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(OWNER_ID).bearer())
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, DataPermissionForbiddenException.ERROR_CODE);
  }

  @Test
  void moveOwnership_self() {
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().getFirst();

    testClient.patch()
        .uri(builder -> builder
            .path(MOVE_OWNERSHIP)
            .queryParam("userId", OWNER_ID)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(OWNER_ID).bearer())
        .exchange()
        .expectStatus().isOk();

    ThirdPartyAppMember owner = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(thirdPartyApp.getId(), OWNER_ID)
        .orElseThrow();
    Assertions.assertTrue(owner.isOwner());
  }
}
