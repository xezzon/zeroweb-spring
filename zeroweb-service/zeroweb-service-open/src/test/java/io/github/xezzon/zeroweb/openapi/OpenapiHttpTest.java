package io.github.xezzon.zeroweb.openapi;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant.ERROR_CODE_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.openapi.entity.AddOpenapiReq;
import io.github.xezzon.zeroweb.openapi.entity.ModifyOpenapiReq;
import io.github.xezzon.zeroweb.openapi.enumeration.HttpMethod;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.exception.PublishedOpenapiCannotBeModifyException;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class OpenapiHttpTest {

  private static final String OPENAPI_ADD_URI = "/openapi";
  private static final String GET_OPENAPI_URI = "/openapi";
  private static final String MODIFY_OPENAPI_URI = "/openapi";
  private static final String PUBLISH_OPENAPI_URI = "/openapi/publish/{id}";

  @Resource
  private OpenapiRepository repository;
  @Resource
  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    for (int i = 0, cnt = Byte.MAX_VALUE; i < cnt; i++) {
      Openapi openapi = new Openapi();
      openapi.setCode(RandomUtil.randomString(8));
      openapi.setDestination(RandomUtil.randomString(8));
      openapi.setHttpMethod(RandomUtil.randomEle(HttpMethod.values()));
      openapi.setStatus(RandomUtil.randomEle(OpenapiStatus.values()));
      repository.save(openapi);
    }
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void addOpenapi() {
    AddOpenapiReq req = new AddOpenapiReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    Id responseBody = webTestClient.post()
        .uri(OPENAPI_ADD_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
    Openapi openapi = repository.findById(responseBody.id()).orElseThrow();
    assertEquals(req.code(), openapi.getCode());
    assertEquals(req.destination(), openapi.getDestination());
    assertEquals(req.httpMethod(), openapi.getHttpMethod());
    assertEquals(OpenapiStatus.DRAFT, openapi.getStatus());
  }

  @Test
  void addOpenapi_repeat() {
    Openapi exist = repository.findAll().get(0);

    AddOpenapiReq req = new AddOpenapiReq(
        exist.getCode(),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.post()
        .uri(OPENAPI_ADD_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, RepeatDataException.ERROR_CODE);
  }

  @Test
  void pagedList() {
    final int top = 5;
    final int skip = top * 2;
    List<Openapi> dataset = repository.findAll();

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
    Openapi draftOne = repository.findAll().stream()
        .filter(openapi -> openapi.getStatus() == OpenapiStatus.DRAFT)
        .findAny().orElseThrow();

    ModifyOpenapiReq req = new ModifyOpenapiReq(
        draftOne.getId(),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
    List<Openapi> dataset = repository.findAll();
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
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, RepeatDataException.ERROR_CODE);
    Openapi openapi = repository.findById(target.getId()).orElseThrow();
    assertEquals(target.getCode(), openapi.getCode());
    assertEquals(target.getDestination(), openapi.getDestination());
    assertEquals(target.getHttpMethod(), openapi.getHttpMethod());
    assertEquals(target.getStatus(), openapi.getStatus());
  }

  @Test
  void modifyOpenapi_noSuchData() {
    ModifyOpenapiReq req = new ModifyOpenapiReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.NO_SUCH_DATA);
  }

  @Test
  void modifyOpenapi_publishedApi() {
    Openapi publishedOpenapi = repository.findAll().stream()
        .filter(openapi -> openapi.getStatus() == OpenapiStatus.PUBLISHED)
        .findAny().orElseThrow();

    ModifyOpenapiReq req = new ModifyOpenapiReq(
        publishedOpenapi.getId(),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader()
        .valueEquals(ERROR_CODE_HEADER, PublishedOpenapiCannotBeModifyException.ERROR_CODE);

    req = new ModifyOpenapiReq(
        publishedOpenapi.getId(),
        publishedOpenapi.getCode(),
        RandomUtil.randomString(8),
        RandomUtil.randomEle(HttpMethod.values())
    );
    webTestClient.put()
        .uri(MODIFY_OPENAPI_URI)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
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
    Openapi target = repository.findAll().get(0);

    webTestClient.put()
        .uri(builder -> builder.path(PUBLISH_OPENAPI_URI)
            .build(target.getId())
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk();
    Openapi openapi = repository.findById(target.getId()).orElseThrow();
    assertEquals(OpenapiStatus.PUBLISHED, openapi.getStatus());
  }

  @Test
  void publishOpenapi_noSuchData() {
    webTestClient.put()
        .uri(builder -> builder.path(PUBLISH_OPENAPI_URI)
            .build(RandomUtil.randomString(8))
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS)
        .expectHeader().valueEquals(ERROR_CODE_HEADER, ErrorCodeConstant.NO_SUCH_DATA);
  }
}
