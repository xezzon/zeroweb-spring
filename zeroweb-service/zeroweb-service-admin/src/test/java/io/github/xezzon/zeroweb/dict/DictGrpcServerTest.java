package io.github.xezzon.zeroweb.dict;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.dict.DictGrpc.DictBlockingStub;
import io.github.xezzon.zeroweb.dict.domain.Dict;
import io.github.xezzon.zeroweb.dict.repository.DictRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author xezzon
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
class DictGrpcServerTest {

  @GrpcClient("dict")
  private DictBlockingStub dictBlockingStub;
  @Resource
  private DictRepository repository;

  List<Dict> initData() {
    List<Dict> dataset = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
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

  @Test
  void getDictListByTag() {
    List<Dict> dataset = this.initData();
    DictListResp resp = dictBlockingStub.getDictListByTag(DictReq.newBuilder()
        .setTag(dataset.get(0).getCode())
        .build()
    );

    List<Dict> children = dataset.get(0).getChildren();
    children.add(children.get(0).getChildren().get(0));
    children.sort(Comparator.comparing(Dict::getOrdinal));
    for (int i = 0, cnt = children.size(); i < cnt; i++) {
      assertEquals(children.get(i).getId(), resp.getData(i).getId());
    }
  }

  @Test
  void importDict() {
    this.initData();
    final List<Dict> dataset = repository.findAll();
    int loopTimes = 4;
    int count = 0;
    List<DictImportReq> tagList = new ArrayList<>();
    for (int i = 0; i < loopTimes; i++) {
      tagList.add(DictImportReq.newBuilder()
          .setCode(RandomUtil.randomString(8))
          .setOrdinal(RandomUtil.randomInt())
          .build()
      );
      count++;
    }
    List<DictImportReq> itemList = new ArrayList<>();
    for (DictImportReq tag : tagList) {
      for (int i = 0; i < loopTimes; i++) {
        itemList.add(DictImportReq.newBuilder()
            .setTag(tag.getCode())
            .setCode(RandomUtil.randomString(8))
            .setOrdinal(RandomUtil.randomInt())
            .build()
        );
        count++;
      }
    }
    Dict existTag = RandomUtil.randomEle(dataset.parallelStream()
        .filter(o -> Objects.equals(o.getTag(), Dict.DICT_TAG))
        .toList()
    );
    tagList.add(0, DictImportReq.newBuilder()
        .setCode(existTag.getCode())
        .setOrdinal(RandomUtil.randomInt())
        .build()
    );
    List<Dict> existItems = RandomUtil.randomEleList(
            dataset.parallelStream()
                .filter(o -> Objects.equals(o.getTag(), existTag.getCode()))
                .toList(),
            loopTimes
        );
    itemList.addAll(existItems.parallelStream()
        .map(o -> DictImportReq.newBuilder()
            .setTag(o.getTag())
            .setCode(o.getCode())
            .setOrdinal(RandomUtil.randomInt())
            .build()
        )
        .toList()
    );
    for (int i = 0; i < loopTimes; i++) {
      itemList.add(DictImportReq.newBuilder()
          .setTag(existTag.getCode())
          .setCode(RandomUtil.randomString(8))
          .setOrdinal(RandomUtil.randomInt())
          .build()
      );
      count++;
    }
    DictImportReqList dictImportReqList = DictImportReqList.newBuilder()
        .addAllData(tagList)
        .addAllData(itemList)
        .build();
    dictBlockingStub.importDict(dictImportReqList);
    assertEquals(dataset.size() + count, repository.count());
    Optional<Dict> result = repository.findByTagAndCode(
        Dict.DICT_TAG,
        tagList.get(0).getCode()
    );
    assertTrue(result.isPresent());
    assertEquals(existTag.getId(), result.get().getId());
    for (Dict item : existItems) {
      result = repository.findByTagAndCode(item.getTag(), item.getCode());
      assertTrue(result.isPresent());
      assertEquals(item.getId(), result.get().getId());
      assertEquals(item.getOrdinal(), result.get().getOrdinal());
    }
  }
}
