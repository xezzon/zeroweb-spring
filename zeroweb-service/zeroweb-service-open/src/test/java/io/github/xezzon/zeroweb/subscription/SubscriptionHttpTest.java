package io.github.xezzon.zeroweb.subscription;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.openapi.enumeration.HttpMethod;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import io.github.xezzon.zeroweb.subscription.entity.AddSubscriptionReq;
import io.github.xezzon.zeroweb.subscription.enumeration.SubscriptionStatus;
import io.github.xezzon.zeroweb.subscription.exception.UnpublishedOpenapiCannotBeSubscribeException;
import io.github.xezzon.zeroweb.subscription.repository.SubscriptionRepository;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMember;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMemberRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class SubscriptionHttpTest {

  private static final String SUBSCRIPTION_LIST_URI = "/third-party-app/{appId}/subscription";
  private static final String SUBSCRIBE_URI = "/subscription";
  private static final String AUDIT_SUBSCRIPTION_URI = "/subscription/audit/{id}";
  private static final String THIRD_PARTY_APP_MEMBER = UUID.randomUUID().toString();

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private SubscriptionRepository repository;
  @Resource
  private OpenapiRepository openapiRepository;
  @Resource
  private ThirdPartyAppRepository thirdPartyAppRepository;
  @Resource
  private ThirdPartyAppMemberRepository thirdPartyAppMemberRepository;

  @BeforeEach
  void setUp() {
    List<Openapi> openapiList = new ArrayList<>(16);
    for (int i = 0, cnt = Byte.MAX_VALUE; i < cnt; i++) {
      Openapi openapi = new Openapi();
      openapi.setCode(RandomUtil.randomString(8));
      openapi.setDestination(RandomUtil.randomString(8));
      openapi.setHttpMethod(RandomUtil.randomEle(HttpMethod.values()));
      openapi.setStatus(RandomUtil.randomEle(OpenapiStatus.values()));
      openapiList.add(openapi);
    }
    openapiRepository.saveAll(openapiList);
    List<Subscription> subscriptionList = new ArrayList<>(8 * 4);
    for (int i = 0, cnt = 8; i < cnt; i++) {
      ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
      thirdPartyApp.setName(RandomUtil.randomString(8));
      thirdPartyApp.setOwnerId(UUID.randomUUID().toString());
      thirdPartyAppRepository.save(thirdPartyApp);
      ThirdPartyAppMember member = new ThirdPartyAppMember();
      member.setGroupId(thirdPartyApp.getId());
      member.setUserId(THIRD_PARTY_APP_MEMBER);
      member.setRoleId(ThirdPartyAppMember.DEFAULT_ROLE_ID);
      thirdPartyAppMemberRepository.save(member);

      RandomUtil.randomEleList(openapiList, 4)
          .stream()
          .map(o -> {
            Subscription subscription = new Subscription();
            subscription.setAppId(thirdPartyApp.getId());
            subscription.setOpenapiCode(o.getCode());
            subscription.setStatus(RandomUtil.randomEle(SubscriptionStatus.values()));
            return subscription;
          })
          .forEach(subscriptionList::add);
    }
    repository.saveAll(subscriptionList);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void listSubscription() {
    final int top = 2000;
    final int skip = 0;
    List<Subscription> dataset = repository.findAll();

    PagedModel<Subscription> responseBody = webTestClient.get()
        .uri(builder -> builder.path(SUBSCRIPTION_LIST_URI)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build(dataset.get(0).getAppId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .id(THIRD_PARTY_APP_MEMBER)
            .bearer()
        )
        .exchange()
        .expectBody(new ParameterizedTypeReference<PagedModel<Subscription>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    List<Openapi> openapiDataset = openapiRepository.findAll();
    openapiDataset = openapiDataset.parallelStream()
        .filter(Openapi::isPublished)
        .sorted(Comparator.comparing(Openapi::getCode))
        .toList();
    Assertions.assertEquals(openapiDataset.size(), responseBody.getPage().getTotalElements());
    openapiDataset = openapiDataset.parallelStream()
        .skip(skip)
        .limit(top)
        .toList();
    Assertions.assertEquals(openapiDataset.size(), responseBody.getContent().size());
    for (int i = 0, cnt = responseBody.getContent().size(); i < cnt; i++) {
      Subscription actual = responseBody.getContent().get(i);
      Openapi openapi = openapiDataset.get(i);
      Assertions.assertEquals(openapi.getId(), actual.getOpenapi().getId());
      Assertions.assertTrue(actual.getOpenapi().isPublished());
      Assertions.assertNull(actual.getOpenapi().getDestination());
      Optional<Subscription> except = dataset.parallelStream()
          .filter(o -> Objects.equals(o.getAppId(), dataset.get(0).getAppId()))
          .filter(o -> Objects.equals(o.getOpenapiCode(), openapi.getCode()))
          .findAny();
      if (except.isPresent()) {
        Assertions.assertEquals(except.get().getId(), actual.getId());
        Assertions.assertEquals(except.get().getAppId(), actual.getAppId());
        Assertions.assertEquals(except.get().getOpenapiCode(), actual.getOpenapiCode());
        Assertions.assertEquals(
            except.get().getSubscriptionStatus(),
            actual.getSubscriptionStatus()
        );
      } else {
        Assertions.assertNull(actual.getId());
        Assertions.assertNull(actual.getAppId());
        Assertions.assertNull(actual.getOpenapiCode());
        Assertions.assertEquals(SubscriptionStatus.NONE, actual.getSubscriptionStatus());
      }
    }
  }

  @Test
  void listSubscription_dataPermission() {
    final int top = 2000;
    final int skip = 0;
    List<Subscription> dataset = repository.findAll();

    webTestClient.get()
        .uri(builder -> builder.path(SUBSCRIPTION_LIST_URI)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build(dataset.get(0).getAppId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator
            .userBuilder()
            .permissions(Collections.emptyList())
            .bearer()
        )
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader()
        .valueEquals(ERROR_CODE_HEADER, DataPermissionForbiddenException.ERROR_CODE);
  }

  @Test
  void subscribe() {
    Openapi openapi = openapiRepository.findAll().stream()
        .filter(Openapi::isPublished)
        .findAny().orElseThrow();
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    AddSubscriptionReq req = new AddSubscriptionReq(thirdPartyApp.getId(), openapi.getCode());
    Id responseBody = webTestClient.post()
        .uri(SUBSCRIBE_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(THIRD_PARTY_APP_MEMBER).bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Optional<Subscription> actual = repository.findById(responseBody.id());
    Assertions.assertTrue(actual.isPresent());
  }

  @Test
  void subscribe_unpublishedOpenapi() {
    Openapi openapi = openapiRepository.findAll().stream()
        .filter(Predicate.not(Openapi::isPublished))
        .findAny().orElseThrow();
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    AddSubscriptionReq req = new AddSubscriptionReq(thirdPartyApp.getId(), openapi.getCode());
    webTestClient.post()
        .uri(SUBSCRIBE_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(THIRD_PARTY_APP_MEMBER).bearer())
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(
            ERROR_CODE_HEADER,
            UnpublishedOpenapiCannotBeSubscribeException.ERROR_CODE
        );
  }

  @Test
  void subscribe_dataPermissionForbidden() {
    Openapi openapi = openapiRepository.findAll().get(0);
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    AddSubscriptionReq req = new AddSubscriptionReq(thirdPartyApp.getId(), openapi.getCode());
    webTestClient.post()
        .uri(SUBSCRIBE_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader()
        .valueEquals(ERROR_CODE_HEADER, DataPermissionForbiddenException.ERROR_CODE);
  }

  @Test
  void auditSubscription() {
    Subscription target = repository.findAll().get(0);

    webTestClient.put()
        .uri(builder -> builder
            .path(AUDIT_SUBSCRIPTION_URI)
            .build(target.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();

    Subscription actual = repository.findById(target.getId()).orElseThrow();
    if (Objects.equals(target.getSubscriptionStatus(), SubscriptionStatus.AUDITING)) {
      Assertions.assertEquals(SubscriptionStatus.SUBSCRIBED, actual.getSubscriptionStatus());
    } else {
      Assertions.assertEquals(target.getSubscriptionStatus(), actual.getSubscriptionStatus());
    }
  }
}
