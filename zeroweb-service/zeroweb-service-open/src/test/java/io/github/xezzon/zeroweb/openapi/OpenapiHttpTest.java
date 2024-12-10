package io.github.xezzon.zeroweb.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.ErrorCode;
import io.github.xezzon.zeroweb.common.exception.OpenErrorCode;
import io.github.xezzon.zeroweb.openapi.domain.AddOpenapiReq;
import io.github.xezzon.zeroweb.openapi.domain.HttpMethod;
import io.github.xezzon.zeroweb.openapi.domain.ModifyOpenapiReq;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import io.github.xezzon.zeroweb.openapi.domain.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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
class OpenapiHttpTest {

  private static final String OPENAPI_ADD_URI = "/openapi/add";
  private static final String GET_OPENAPI_URI = "/openapi";
  private static final String MODIFY_OPENAPI_URI = "/openapi/update";
  private static final String PUBLISH_OPENAPI_URI = "/openapi/publish/{id}";

  @Resource
  private OpenapiRepository repository;
  @Resource
  private WebTestClient webTestClient;

  public List<Openapi> initData() {
    ArrayList<Openapi> dataset = new ArrayList<>();
    for (int i = 0, cnt = Byte.MAX_VALUE; i < cnt; i++) {
      Openapi openapi = new Openapi();
      openapi.setCode(RandomUtil.randomString(8));
      openapi.setDestination(RandomUtil.randomString(8));
      openapi.setHttpMethod(RandomUtil.randomEle(HttpMethod.values()));
      openapi.setStatus(RandomUtil.randomEle(OpenapiStatus.values()));
      repository.save(openapi);
      dataset.add(openapi);
    }
    return dataset;
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void addOpenapi() {
    AddOpenapiReq req = new AddOpenapiReq();
    req.setCode(RandomUtil.randomString(8));
    req.setDestination(RandomUtil.randomString(8));
    req.setHttpMethod(RandomUtil.randomEle(HttpMethod.values()));
    Id responseBody = webTestClient.post()
        .uri(OPENAPI_ADD_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
    Openapi openapi = repository.findById(responseBody.id()).orElseThrow();
    assertEquals(req.getCode(), openapi.getCode());
    assertEquals(req.getDestination(), openapi.getDestination());
    assertEquals(req.getHttpMethod(), openapi.getHttpMethod());
    assertEquals(OpenapiStatus.DRAFT, openapi.getStatus());
  }

  @Test
  void addOpenapi_repeat() {
    this.initData();
    Openapi exist = repository.findAll().get(0);

    AddOpenapiReq req = new AddOpenapiReq();
    req.setCode(exist.getCode());
    req.setDestination(RandomUtil.randomString(8));
    req.setHttpMethod(RandomUtil.randomEle(HttpMethod.values()));
    webTestClient.post()
        .uri(OPENAPI_ADD_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.REPEAT_DATA.code());
  }

  @Test
  void pagedList() {
    final int top = 2;
    final int skip = 4;
    List<Openapi> dataset = this.initData();

    PagedModel<Openapi> responseBody = webTestClient.get()
        .uri(builder -> builder
            .path(GET_OPENAPI_URI)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<PagedModel<Openapi>>() {
        })
        .returnResult().getResponseBody();

    assertNotNull(responseBody);
    assertEquals(dataset.size(), responseBody.getPage().getTotalElements());
    List<Openapi> except = dataset.parallelStream()
        .sorted(Comparator.comparing(Openapi::getCode))
        .skip(skip)
        .limit(top)
        .toList();
    for (int i = 0, cnt = responseBody.getContent().size(); i < cnt; i++) {
      assertEquals(except.get(i).getId(), responseBody.getContent().get(i).getId());
    }
  }

  @Test
  void modifyOpenapi() {
    Openapi draftOne = null;
    while (draftOne == null) {
      List<Openapi> dataset = this.initData();
      draftOne = dataset.parallelStream()
          .filter(openapi -> openapi.getStatus() == OpenapiStatus.DRAFT)
          .findAny()
          .orElse(null);
    }

    ModifyOpenapiReq req = new ModifyOpenapiReq(
        draftOne.getId(),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk();
    Openapi openapi = repository.findById(draftOne.getId()).orElseThrow();
    assertEquals(req.code(), openapi.getCode());
    assertEquals(req.destination(), openapi.getDestination());
    assertEquals(req.httpMethod(), openapi.getHttpMethod());
    assertEquals(draftOne.getStatus(), openapi.getStatus());
  }

  @Test
  void modifyOpenapi_repeat() {
    List<Openapi> dataset = this.initData();
    Openapi target = dataset.get(0);
    Openapi repeated = dataset.get(1);

    ModifyOpenapiReq req = new ModifyOpenapiReq(
        target.getId(),
        repeated.getCode(),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.REPEAT_DATA.code());
    Openapi openapi = repository.findById(target.getId()).orElseThrow();
    assertEquals(target.getCode(), openapi.getCode());
    assertEquals(target.getDestination(), openapi.getDestination());
    assertEquals(target.getHttpMethod(), openapi.getHttpMethod());
    assertEquals(target.getStatus(), openapi.getStatus());
  }

  @Test
  void modifyOpenapi_noSuchData() {
    this.initData();

    ModifyOpenapiReq req = new ModifyOpenapiReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.NO_SUCH_DATA.code());
  }

  @Test
  void modifyOpenapi_publishedApi() {
    Openapi publishedOpenapi = null;
    while (publishedOpenapi == null) {
      List<Openapi> dataset = this.initData();
      publishedOpenapi = dataset.parallelStream()
          .filter(openapi -> openapi.getStatus() == OpenapiStatus.PUBLISHED)
          .findAny()
          .orElse(null);
    }

    ModifyOpenapiReq req = new ModifyOpenapiReq(
        publishedOpenapi.getId(),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY.code());

    req = new ModifyOpenapiReq(
        publishedOpenapi.getId(),
        publishedOpenapi.getCode(),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk();
    Openapi openapi = repository.findById(publishedOpenapi.getId()).orElseThrow();
    assertEquals(req.code(), openapi.getCode());
    assertEquals(req.destination(), openapi.getDestination());
    assertEquals(req.httpMethod(), openapi.getHttpMethod());
    assertEquals(publishedOpenapi.getStatus(), openapi.getStatus());
  }

  @Test
  void publishOpenapi() {
    Openapi target = this.initData().get(0);

    webTestClient.put()
        .uri(builder -> builder.path(PUBLISH_OPENAPI_URI)
            .build(target.getId())
        )
        .exchange()
        .expectStatus().isOk();
    Openapi openapi = repository.findById(target.getId()).orElseThrow();
    assertEquals(OpenapiStatus.PUBLISHED, openapi.getStatus());
  }

  @Test
  void publishOpenapi_noSuchData() {
    this.initData();

    webTestClient.put()
        .uri(builder -> builder.path(PUBLISH_OPENAPI_URI)
            .build(RandomUtil.randomString(8))
        )
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.NO_SUCH_DATA.code());
  }
}
