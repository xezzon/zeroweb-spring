package io.github.xezzon.zeroweb.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.ErrorCode;
import io.github.xezzon.zeroweb.common.exception.OpenErrorCode;
import io.github.xezzon.zeroweb.openapi.domain.AddOpenapiReq;
import io.github.xezzon.zeroweb.openapi.domain.ModifyOpenapiReq;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import io.github.xezzon.zeroweb.openapi.domain.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    Id responseBody = webTestClient.post()
        .uri(OPENAPI_ADD_URI)
        .bodyValue(req)
        .exchange()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
    Optional<Openapi> openapi = repository.findById(responseBody.id());
    assertTrue(openapi.isPresent());
    assertEquals(req.getCode(), openapi.get().getCode());
    assertEquals(OpenapiStatus.DRAFT, openapi.get().getStatus());
  }

  @Test
  void addOpenapi_repeat() {
    this.initData();
    Openapi exist = repository.findAll().get(0);

    AddOpenapiReq req = new AddOpenapiReq();
    req.setCode(exist.getCode());
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
  void modifyDict() {
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
        RandomUtil.randomString(8)
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk();
    Optional<Openapi> openapi = repository.findById(draftOne.getId());
    assertTrue(openapi.isPresent());
    assertEquals(req.code(), openapi.get().getCode());
    assertEquals(draftOne.getStatus(), openapi.get().getStatus());
  }

  @Test
  void modifyDict_repeat() {
    List<Openapi> dataset = this.initData();
    Openapi target = dataset.get(0);
    Openapi repeated = dataset.get(1);

    ModifyOpenapiReq req = new ModifyOpenapiReq(
        target.getId(),
        repeated.getCode()
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.REPEAT_DATA.code());
    Optional<Openapi> dict = repository.findById(target.getId());
    assertTrue(dict.isPresent());
    assertEquals(target.getCode(), dict.get().getCode());
    assertEquals(target.getStatus(), dict.get().getStatus());
  }

  @Test
  void modifyDict_noSuchData() {
    this.initData();

    ModifyOpenapiReq req = new ModifyOpenapiReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8)
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
  void modifyDict_publishedApiCannotBeModify() {
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
        RandomUtil.randomString(8)
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(OpenErrorCode.PUBLISHED_OPENAPI_CANNOT_BE_MODIFY.code());
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
    Optional<Openapi> openapi = repository.findById(target.getId());
    assertEquals(OpenapiStatus.PUBLISHED, openapi.orElseThrow().getStatus());
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
