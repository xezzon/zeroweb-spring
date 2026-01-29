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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ThirdPartyAppMemberHttpTest {

  private static final String INVITE_MEMBER = "/third-party-app/{appId}/member";
  private static final String ACCEPT_INVITATION = "/third-party-app/-/member";
  private static final String LIST_MEMBER = "/third-party-app/{appId}/member";
  private static final String MOVE_OWNERSHIP = "/third-party-app/{appId}/owner";
  private static final String DELETE_MEMBER = "/third-party-app/{appId}/member/{id}";

  private final ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
  private final ThirdPartyAppMember owner = new ThirdPartyAppMember();
  private final ThirdPartyAppMember member = new ThirdPartyAppMember();

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
    final String ownerUserId = UUID.randomUUID().toString();
    thirdPartyApp.setName(RandomUtil.randomString(8));
    thirdPartyApp.setOwnerId(ownerUserId);
    thirdPartyAppRepository.save(thirdPartyApp);
    String secretKey = Base64.getEncoder().encodeToString(SIG.HS256.key().build().getEncoded());
    accessSecretRepository
        .updateSecretKeyById(thirdPartyApp.getId(), secretKey);

    owner.setGroupId(thirdPartyApp.getId());
    owner.setUserId(ownerUserId);
    owner.setRoleId(ThirdPartyAppMember.OWNER_ROLE_ID);
    thirdPartyAppMemberRepository.save(owner);
    member.setGroupId(thirdPartyApp.getId());
    member.setUserId(UUID.randomUUID().toString());
    member.setRoleId(ThirdPartyAppMember.DEFAULT_ROLE_ID);
    thirdPartyAppMemberRepository.save(member);
  }

  @AfterEach
  void tearDown() {
    thirdPartyAppRepository.deleteAll();
    thirdPartyAppMemberRepository.deleteAll();
  }

  @Test
  void addMember_general() {
    String token = testClient.post()
        .uri(builder -> builder
            .path(INVITE_MEMBER)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
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

    String token = testClient.post()
        .uri(builder -> builder
            .path(INVITE_MEMBER)
            .queryParam("userId", invitedUser)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
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
        .expectBody(new ParameterizedTypeReference<@NonNull List<ThirdPartyAppMember>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(3, responseBody.size());
  }

  @Test
  void addMember_timeout() {
    String token = testClient.post()
        .uri(builder -> builder
            .path(INVITE_MEMBER)
            .queryParam("timeout", 0)
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
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
    testClient.patch()
        .uri(builder -> builder
            .path(MOVE_OWNERSHIP)
            .queryParam("userId", member.getUserId())
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
        .exchange()
        .expectStatus().isOk();

    ThirdPartyAppMember oldOwner = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(thirdPartyApp.getId(), owner.getUserId())
        .orElseThrow();
    Assertions.assertFalse(oldOwner.isOwner());
    ThirdPartyAppMember newOwner = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(thirdPartyApp.getId(), member.getUserId())
        .orElseThrow();
    Assertions.assertTrue(newOwner.isOwner());
  }

  @Test
  void moveOwnership_notOwner() {
    testClient.patch()
        .uri(builder -> builder
            .path(MOVE_OWNERSHIP)
            .queryParam("userId", owner.getUserId())
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(member.getUserId()).bearer())
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, DataPermissionForbiddenException.ERROR_CODE);
  }

  @Test
  void moveOwnership_notMember() {
    testClient.patch()
        .uri(builder -> builder
            .path(MOVE_OWNERSHIP)
            .queryParam("userId", UUID.randomUUID().toString())
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, DataPermissionForbiddenException.ERROR_CODE);
  }

  @Test
  void moveOwnership_self() {
    testClient.patch()
        .uri(builder -> builder
            .path(MOVE_OWNERSHIP)
            .queryParam("userId", owner.getUserId())
            .build(thirdPartyApp.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
        .exchange()
        .expectStatus().isOk();

    ThirdPartyAppMember newOwner = thirdPartyAppMemberRepository
        .findByGroupIdAndUserId(thirdPartyApp.getId(), owner.getUserId())
        .orElseThrow();
    Assertions.assertTrue(newOwner.isOwner());
  }

  @Test
  void deleteMember() {
    testClient.delete()
        .uri(DELETE_MEMBER, thirdPartyApp.getId(), member.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
        .exchange()
        .expectStatus().isOk();

    Assertions.assertFalse(thirdPartyAppMemberRepository.existsById(member.getId()));
  }

  @Test
  void deleteMember_anotherMember() {
    ThirdPartyAppMember otherMember = new ThirdPartyAppMember();
    otherMember.setGroupId(UUID.randomUUID().toString());
    otherMember.setUserId(member.getUserId());
    otherMember.setRoleId(ThirdPartyAppMember.DEFAULT_ROLE_ID);
    thirdPartyAppMemberRepository.save(otherMember);

    testClient.delete()
        .uri(DELETE_MEMBER, thirdPartyApp.getId(), otherMember.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void deleteMember_owner() {
    testClient.delete()
        .uri(DELETE_MEMBER, thirdPartyApp.getId(), owner.getId())
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(owner.getUserId()).bearer())
        .exchange()
        .expectStatus().isForbidden();
  }
}
