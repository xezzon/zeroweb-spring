package io.github.xezzon.geom.dict;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.domain.Id;
import io.github.xezzon.geom.common.exception.ErrorCode;
import io.github.xezzon.geom.dict.domain.AddDictReq;
import io.github.xezzon.geom.dict.domain.Dict;
import io.github.xezzon.geom.dict.repository.DictRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class DictHttpTest {

  private static final String ADD_DICT_URI = "/dict/add";

  private final List<Dict> dataset = new ArrayList<>();
  @Resource
  private WebTestClient webTestClient;
  @Resource
  private DictRepository repository;

  @BeforeEach
  void setUp() {
    Dict parent = new Dict();
    parent.setTag(Dict.DICT_TAG);
    parent.setCode(RandomUtil.randomString(8));
    parent.setLabel(RandomUtil.randomString(8));
    parent.setParentId(DatabaseConstant.ROOT_ID);
    parent.setOrdinal(RandomUtil.randomInt());
    parent.setEnabled(true);
    repository.save(parent);
    dataset.add(parent);
    Dict child = new Dict();
    child.setTag(parent.getCode());
    child.setCode(RandomUtil.randomString(8));
    child.setLabel(RandomUtil.randomString(8));
    child.setParentId(parent.getId());
    child.setOrdinal(RandomUtil.randomInt());
    child.setEnabled(true);
    repository.save(child);
    dataset.add(child);
  }

  @Test
  void addDictTag() {
    AddDictReq req = new AddDictReq();
    req.setCode(RandomUtil.randomString(8));
    req.setLabel(RandomUtil.randomString(8));
    req.setOrdinal(RandomUtil.randomInt());
    Id responseBody = webTestClient.post()
        .uri(ADD_DICT_URI)
        .bodyValue(req)
        .exchange()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
    Optional<Dict> dict = repository.findById(responseBody.id());
    assertTrue(dict.isPresent());
    assertEquals(Dict.DICT_TAG, dict.get().getTag());
    assertEquals(DatabaseConstant.ROOT_ID, dict.get().getParentId());
  }

  @Test
  void addDictTag_repeat() {
    Dict exist = dataset.get(0);

    AddDictReq req = new AddDictReq();
    req.setCode(exist.getCode());
    req.setLabel(RandomUtil.randomString(8));
    req.setOrdinal(RandomUtil.randomInt());
    webTestClient.post()
        .uri(ADD_DICT_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.REPEAT_DATA.code());
  }

  @Test
  void addDict() {
    Dict parent = dataset.get(0);
    AddDictReq req = new AddDictReq();
    req.setTag(parent.getCode());
    req.setCode(RandomUtil.randomString(8));
    req.setLabel(RandomUtil.randomString(8));
    req.setParentId(parent.getId());
    req.setOrdinal(RandomUtil.randomInt());
    Id responseBody = webTestClient.post()
        .uri(ADD_DICT_URI)
        .bodyValue(req)
        .exchange()
        .expectBody(Id.class)
        .returnResult().getResponseBody();
    assertNotNull(responseBody);
    assertNotNull(responseBody.id());
    Optional<Dict> dict = repository.findById(responseBody.id());
    assertTrue(dict.isPresent());
  }

  @Test
  void addDict_repeat() {
    Dict exist = dataset.get(1);

    AddDictReq req = new AddDictReq();
    req.setTag(exist.getTag());
    req.setCode(exist.getCode());
    req.setLabel(RandomUtil.randomString(8));
    req.setParentId(exist.getParentId());
    req.setOrdinal(RandomUtil.randomInt());
    webTestClient.post()
        .uri(ADD_DICT_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.REPEAT_DATA.code());
  }
}
