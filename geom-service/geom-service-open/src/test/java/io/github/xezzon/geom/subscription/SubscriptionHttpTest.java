package io.github.xezzon.geom.subscription;

import static com.google.auth.http.AuthHttpConstants.AUTHORIZATION;
import static io.github.xezzon.geom.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.geom.common.exception.GlobalExceptionHandler.ERROR_CODE_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.auth.TestJwtGenerator;
import io.github.xezzon.geom.common.exception.OpenErrorCode;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.common.domain.PagedModel;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.openapi.domain.Openapi;
import io.github.xezzon.geom.openapi.domain.OpenapiStatus;
import io.github.xezzon.geom.openapi.repository.OpenapiRepository;
import io.github.xezzon.geom.subscription.domain.AddSubscriptionReq;
import io.github.xezzon.geom.subscription.domain.Subscription;
import io.github.xezzon.geom.subscription.domain.SubscriptionStatus;
import io.github.xezzon.geom.subscription.repository.SubscriptionRepository;
import io.github.xezzon.geom.third_party_app.domain.ThirdPartyApp;
import io.github.xezzon.geom.third_party_app.repository.ThirdPartyAppRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class SubscriptionHttpTest {

  private static final String SUBSCRIPTION_LIST_URI = "/third-party-app/{appId}/subscription";
  private static final String SUBSCRIBE_URI = "/subscription";
  private static final String AUDIT_SUBSCRIPTION_URI = "/subscription/audit/{id}";
  private static final String THIRD_PARTY_APP_OWNER = RandomUtil.randomString(8);

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private SubscriptionRepository repository;
  @Resource
  private OpenapiRepository openapiRepository;
  @Resource
  private ThirdPartyAppRepository thirdPartyAppRepository;

  List<Subscription> initData() {
    List<Openapi> openapiList = new ArrayList<>(16);
    for (int i = 0, cnt = 16; i < cnt; i++) {
      Openapi openapi = new Openapi();
      openapi.setCode(RandomUtil.randomString(8));
      openapi.setStatus(RandomUtil.randomEle(OpenapiStatus.values()));
      openapiList.add(openapi);
    }
    openapiRepository.saveAll(openapiList);
    List<Subscription> subscriptionList = new ArrayList<>(8 * 4);
    for (int i = 0, cnt = 8; i < cnt; i++) {
      ThirdPartyApp thirdPartyApp = new ThirdPartyApp();
      thirdPartyApp.setName(RandomUtil.randomString(8));
      thirdPartyApp.setOwnerId(THIRD_PARTY_APP_OWNER);
      thirdPartyAppRepository.save(thirdPartyApp);

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
    return subscriptionList;
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void listSubscription() {
    final int top = 2000;
    final int skip = 0;
    List<Subscription> dataset = this.initData();

    webTestClient.get()
        .uri(builder -> builder.path(SUBSCRIPTION_LIST_URI)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build(dataset.get(0).getAppId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer(RandomUtil.randomString(6)))
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCode.DATA_PERMISSION_FORBIDDEN.code());

    PagedModel<Subscription> responseBody = webTestClient.get()
        .uri(builder -> builder.path(SUBSCRIPTION_LIST_URI)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build(dataset.get(0).getAppId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer(THIRD_PARTY_APP_OWNER))
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
  void subscribe() {
    Openapi openapi;
    do {
      this.initData();
      openapi = openapiRepository.findAll().parallelStream()
          .filter(Openapi::isPublished)
          .findAny()
          .orElse(null);
    } while (openapi == null);
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    AddSubscriptionReq req = new AddSubscriptionReq(thirdPartyApp.getId(), openapi.getCode());
    Id responseBody = webTestClient.post()
        .uri(SUBSCRIBE_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer(THIRD_PARTY_APP_OWNER))
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
    Openapi openapi;
    do {
      this.initData();
      openapi = openapiRepository.findAll().parallelStream()
          .filter(o -> !o.isPublished())
          .findAny()
          .orElse(null);
    } while (openapi == null);
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    AddSubscriptionReq req = new AddSubscriptionReq(thirdPartyApp.getId(), openapi.getCode());
    webTestClient.post()
        .uri(SUBSCRIBE_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer(THIRD_PARTY_APP_OWNER))
        .exchange()
        .expectStatus().isBadRequest()
        .expectHeader().valueEquals(
            ERROR_CODE_HEADER,
            OpenErrorCode.UNPUBLISHED_OPENAPI_CANNOT_BE_SUBSCRIBE.code()
        );
  }

  @Test
  void subscribe_dataPermissionForbidden() {
    this.initData();
    Openapi openapi = openapiRepository.findAll().get(0);
    ThirdPartyApp thirdPartyApp = thirdPartyAppRepository.findAll().get(0);

    AddSubscriptionReq req = new AddSubscriptionReq(thirdPartyApp.getId(), openapi.getCode());
    webTestClient.post()
        .uri(SUBSCRIBE_URI)
        .bodyValue(req)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.generateBearer(RandomUtil.randomString(8)))
        .exchange()
        .expectStatus().isForbidden()
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCode.DATA_PERMISSION_FORBIDDEN.code());
  }

  @Test
  void auditSubscription() {
    Subscription target = this.initData().get(0);

    webTestClient.put()
        .uri(builder -> builder
            .path(AUDIT_SUBSCRIPTION_URI)
            .build(target.getId())
        )
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
