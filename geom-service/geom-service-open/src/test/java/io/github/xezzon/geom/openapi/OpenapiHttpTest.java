package io.github.xezzon.geom.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.common.domain.PagedModel;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.openapi.domain.AddOpenapiReq;
import io.github.xezzon.geom.openapi.domain.Openapi;
import io.github.xezzon.geom.openapi.domain.OpenapiStatus;
import io.github.xezzon.geom.openapi.repository.OpenapiRepository;
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
}
