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
import io.github.xezzon.geom.dict.domain.ModifyDictReq;
import io.github.xezzon.geom.dict.repository.DictRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.RepeatedTest;
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
  private static final String MODIFY_DICT_URI = "/dict/update";

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private DictRepository repository;

  List<Dict> initData() {
    List<Dict> dataset = new ArrayList<>();
    for (int i = 0; i < 2; i++) {
      Dict parent = new Dict();
      parent.setTag(Dict.DICT_TAG);
      parent.setCode(RandomUtil.randomString(8));
      parent.setLabel(RandomUtil.randomString(8));
      parent.setParentId(DatabaseConstant.ROOT_ID);
      parent.setOrdinal(RandomUtil.randomInt());
      parent.setEnabled(true);
      repository.save(parent);
      dataset.add(parent);
      List<Dict> children = new ArrayList<>();
      for (int j = 0; j < 2; j++) {
        Dict child = new Dict();
        child.setTag(parent.getCode());
        child.setCode(RandomUtil.randomString(8));
        child.setLabel(RandomUtil.randomString(8));
        child.setParentId(parent.getId());
        child.setOrdinal(RandomUtil.randomInt());
        child.setEnabled(true);
        repository.save(child);
        children.add(child);
      }
      parent.setChildren(children);
    }
    return dataset;
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
    Dict exist = this.initData().get(0);

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
    Dict parent = this.initData().get(0);
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
    Dict parent = this.initData().get(0);
    Dict exist = parent.getChildren().get(0);

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

  @RepeatedTest(10)
  void modifyDict() {
    Dict target = this.initData().get(0);

    ModifyDictReq req = new ModifyDictReq();
    req.setId(target.getId());
    req.setCode(RandomUtil.randomString(8));
    req.setLabel(RandomUtil.randomString(8));
    req.setOrdinal(RandomUtil.randomInt());
    req.setParentId(target.getParentId());
    req.setEnabled(RandomUtil.randomBoolean());
    webTestClient.put()
        .uri(MODIFY_DICT_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk();
    Optional<Dict> dict = repository.findById(target.getId());
    assertTrue(dict.isPresent());
    assertEquals(req.getCode(), dict.get().getCode());
    assertEquals(target.getTag(), dict.get().getTag());
    assertEquals(req.getLabel(), dict.get().getLabel());
    assertEquals(req.getOrdinal(), dict.get().getOrdinal());
    assertEquals(target.getParentId(), dict.get().getParentId());
    assertEquals(req.getEnabled(), dict.get().getEnabled());
  }

  @RepeatedTest(10)
  void modifyDict_repeat() {
    Dict target = this.initData().get(0);
    Dict repeated = this.initData().get(1);
    if (RandomUtil.randomBoolean()) {
      repeated = target.getChildren().get(1);
      target = target.getChildren().get(0);
    }

    ModifyDictReq req = new ModifyDictReq();
    req.setId(target.getId());
    req.setCode(repeated.getCode());
    req.setLabel(RandomUtil.randomString(8));
    req.setOrdinal(RandomUtil.randomInt());
    req.setParentId(target.getParentId());
    req.setEnabled(RandomUtil.randomBoolean());
    webTestClient.put()
        .uri(MODIFY_DICT_URI)
        .bodyValue(req)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.code").isEqualTo(ErrorCode.REPEAT_DATA.code());
    Optional<Dict> dict = repository.findById(target.getId());
    assertTrue(dict.isPresent());
    assertEquals(target.getCode(), dict.get().getCode());
    assertEquals(target.getTag(), dict.get().getTag());
    assertEquals(target.getLabel(), dict.get().getLabel());
    assertEquals(target.getOrdinal(), dict.get().getOrdinal());
    assertEquals(target.getParentId(), dict.get().getParentId());
    assertEquals(target.getEnabled(), dict.get().getEnabled());
  }
}
