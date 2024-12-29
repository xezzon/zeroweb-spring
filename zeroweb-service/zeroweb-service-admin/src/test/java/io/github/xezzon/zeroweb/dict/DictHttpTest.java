package io.github.xezzon.zeroweb.dict;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.ErrorCode;
import io.github.xezzon.zeroweb.dict.domain.Dict;
import io.github.xezzon.zeroweb.dict.entity.AddDictReq;
import io.github.xezzon.zeroweb.dict.entity.ModifyDictReq;
import io.github.xezzon.zeroweb.dict.repository.DictRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class DictHttpTest {

  private static final String ADD_DICT_URI = "/dict";
  private static final String GET_DICT_TREE_BY_TAG_URI = "/dict/tag/{tag}";
  private static final String MODIFY_DICT_URI = "/dict";
  private static final String UPDATE_DICT_STATUS_URI = "/dict/update-status";
  private static final String DELETE_DICT_URI = "/dict";
  private static final String GET_DICT_URI = "/dict";

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private DictRepository repository;

  List<Dict> initData() {
    List<Dict> dataset = new ArrayList<>();
    for (int i = 0; i < 16; i++) {
      Dict parent = new Dict();
      parent.setTag(Dict.DICT_TAG);
      parent.setCode(RandomUtil.randomString(8));
      parent.setLabel(RandomUtil.randomString(8));
      parent.setParentId(DatabaseConstant.ROOT_ID);
      parent.setOrdinal(RandomUtil.randomInt());
      parent.setEnabled(true);
      parent.setEditable(true);
      repository.save(parent);
      dataset.add(parent);
      List<Dict> children = new ArrayList<>();
      for (int j = 0; j < 4; j++) {
        Dict child = new Dict();
        child.setTag(parent.getCode());
        child.setCode(RandomUtil.randomString(8));
        child.setLabel(RandomUtil.randomString(8));
        child.setParentId(parent.getId());
        child.setOrdinal(RandomUtil.randomInt());
        child.setEnabled(true);
        child.setEditable(true);
        repository.save(child);
        children.add(child);
      }
      parent.setChildren(children);
      Dict child = children.get(0);
      Dict grandchild = new Dict();
      grandchild.setTag(child.getTag());
      grandchild.setCode(RandomUtil.randomString(8));
      grandchild.setLabel(RandomUtil.randomString(8));
      grandchild.setParentId(child.getId());
      grandchild.setOrdinal(RandomUtil.randomInt());
      grandchild.setEnabled(true);
      grandchild.setEditable(true);
      repository.save(grandchild);
      child.setChildren(Collections.singletonList(grandchild));
    }
    return dataset;
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
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
    Dict dict = repository.findById(responseBody.id()).orElseThrow();
    assertEquals(Dict.DICT_TAG, dict.getTag());
    assertEquals(DatabaseConstant.ROOT_ID, dict.getParentId());
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

  @Test
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
    Dict dict = repository.findById(target.getId()).orElseThrow();
    assertEquals(req.getCode(), dict.getCode());
    assertEquals(target.getTag(), dict.getTag());
    assertEquals(req.getLabel(), dict.getLabel());
    assertEquals(req.getOrdinal(), dict.getOrdinal());
    assertEquals(target.getParentId(), dict.getParentId());
    assertEquals(req.getEnabled(), dict.getEnabled());
  }

  @Test
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
    Dict dict = repository.findById(target.getId()).orElseThrow();
    assertEquals(target.getCode(), dict.getCode());
    assertEquals(target.getTag(), dict.getTag());
    assertEquals(target.getLabel(), dict.getLabel());
    assertEquals(target.getOrdinal(), dict.getOrdinal());
    assertEquals(target.getParentId(), dict.getParentId());
    assertEquals(target.getEnabled(), dict.getEnabled());
  }

  @Test
  void updateDictStatus() {
    List<Dict> dataset = this.initData();
    dataset.addAll(dataset.stream()
        .map(Dict::getChildren)
        .flatMap(List::stream)
        .toList()
    );
    Collections.shuffle(dataset);

    webTestClient.put()
        .uri(builder -> builder
            .path(UPDATE_DICT_STATUS_URI)
            .queryParam("enabled", false)
            .build()
        )
        .bodyValue(List.of(dataset.get(0).getId(), dataset.get(1).getId()))
        .exchange()
        .expectStatus().isOk();
    Dict dict1 = repository.findById(dataset.get(0).getId()).orElseThrow();
    assertEquals(false, dict1.getEnabled());
    Dict dict2 = repository.findById(dataset.get(1).getId()).orElseThrow();
    assertEquals(false, dict2.getEnabled());

    webTestClient.put()
        .uri(builder -> builder
            .path(UPDATE_DICT_STATUS_URI)
            .queryParam("enabled", true)
            .build()
        )
        .bodyValue(List.of(dataset.get(1).getId(), dataset.get(2).getId()))
        .exchange()
        .expectStatus().isOk();
    Dict dict3 = repository.findById(dataset.get(1).getId()).orElseThrow();
    assertEquals(true, dict3.getEnabled());
    Dict dict4 = repository.findById(dataset.get(2).getId()).orElseThrow();
    assertEquals(true, dict4.getEnabled());
  }

  @Test
  void removeDictTag() {
    List<Dict> dataset = this.initData();
    Collections.shuffle(dataset);

    Dict dict0 = dataset.get(0);
    Dict dict1 = dataset.get(1);
    webTestClient.method(HttpMethod.DELETE)
        .uri(DELETE_DICT_URI)
        .bodyValue(List.of(dict0.getId(), dict1.getId()))
        .exchange()
        .expectStatus().isOk();
    assertFalse(repository.existsById(dict0.getId()));
    assertFalse(repository.existsById(dict1.getId()));
    for (Dict child : dict0.getChildren()) {
      assertFalse(repository.existsById(child.getId()));
    }
  }

  @Test
  void removeDictItem() {
    List<Dict> dataset = this.initData();
    Collections.shuffle(dataset);

    Dict dict20 = dataset.get(2).getChildren().get(0);
    Dict dict22 = dataset.get(2).getChildren().get(2);
    Dict dict31 = dataset.get(3).getChildren().get(1);
    webTestClient.method(HttpMethod.DELETE)
        .uri(DELETE_DICT_URI)
        .bodyValue(List.of(
            dict20.getId(),
            dict22.getId(),
            dict31.getId()
        ))
        .exchange()
        .expectStatus().isOk();
    assertFalse(repository.existsById(dict20.getId()));
    assertFalse(repository.existsById(dict20.getChildren().get(0).getId())); // 子级被删除
    assertFalse(repository.existsById(dict22.getId()));
    assertFalse(repository.existsById(dict31.getId()));
    assertTrue(repository.existsById(dataset.get(2).getId()));
    assertTrue(repository.existsById(dataset.get(3).getChildren().get(0).getId()));
  }
  
  @Test
  void getDictTreeByTag() {
    List<Dict> dataset = this.initData();
    List<Dict> responseBody = webTestClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(GET_DICT_TREE_BY_TAG_URI)
            .build(dataset.get(0).getCode())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Dict.class)
        .returnResult().getResponseBody();

    assertNotNull(responseBody);
    List<Dict> children = dataset.get(0).getChildren();
    children.sort(Comparator.comparing(Dict::getOrdinal));
    for (int i = 0, cnt = responseBody.size(); i < cnt; i++) {
      assertEquals(children.get(i).getId(), responseBody.get(i).getId());
    }
    List<Dict> exceptGrandchildren = children.parallelStream()
        .map(Dict::getChildren)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .toList();
    List<Dict> grandchildren = responseBody.parallelStream()
        .map(Dict::getChildren)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .toList();
    for (int i = 0, cnt = exceptGrandchildren.size(); i < cnt; i++) {
      assertEquals(exceptGrandchildren.get(i).getId(), grandchildren.get(i).getId());
    }
  }

  @Test
  void pagedList() {
    final int top = 5;
    final int skip = top * 2;
    List<Dict> dataset = this.initData();

    PagedModel<Dict> responseBody = webTestClient.get()
        .uri(builder -> builder
            .path(GET_DICT_URI)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<PagedModel<Dict>>() {
        })
        .returnResult().getResponseBody();

    assertNotNull(responseBody);
    assertEquals(dataset.size(), responseBody.getPage().getTotalElements());
    List<Dict> except = dataset.parallelStream()
        .sorted(Comparator.comparing(Dict::getCode))
        .skip(skip)
        .limit(top)
        .toList();
    for (int i = 0, cnt = responseBody.getContent().size(); i < cnt; i++) {
      assertEquals(except.get(i).getId(), responseBody.getContent().get(i).getId());
    }
  }
}
