package io.github.xezzon.geom.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.openapi.domain.AddOpenapiReq;
import io.github.xezzon.geom.openapi.domain.Openapi;
import io.github.xezzon.geom.openapi.domain.OpenapiStatus;
import io.github.xezzon.geom.openapi.repository.OpenapiRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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
}
