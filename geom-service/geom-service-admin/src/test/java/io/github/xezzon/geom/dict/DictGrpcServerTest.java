package io.github.xezzon.geom.dict;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.dict.DictGrpc.DictBlockingStub;
import io.github.xezzon.geom.dict.domain.Dict;
import io.github.xezzon.geom.dict.repository.DictRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

  @GrpcClient("inProcess")
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
      repository.save(grandchild);
      child.setChildren(Collections.singletonList(grandchild));
    }
    return dataset;
  }

  @Test
  @DirtiesContext
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
}
