package io.github.xezzon.zeroweb.setting;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import io.github.xezzon.zeroweb.setting.SettingGrpc.SettingBlockingStub;
import io.github.xezzon.zeroweb.setting.TestEntity.Child;
import io.github.xezzon.zeroweb.setting.repository.SettingRepository;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import tools.jackson.databind.ObjectMapper;

/**
 * @author xezzon
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class SettingGrpcTest {

  private static String testSchema;

  private final Setting expect = new Setting();
  private final TestEntity expectValue = new TestEntity();
  @Resource
  private SettingBlockingStub settingBlockingStub;
  @Resource
  private SettingRepository repository;
  @Resource
  private ObjectMapper objectMapper;

  @BeforeAll
  static void beforeAll() throws IOException {
    Path path = ResourceUtil.getResourceFromClasspath("setting-schema.json");
    testSchema = Files.readString(path);
  }

  @BeforeEach
  void setUp() {
    expect.setKey(RandomUtil.randomString(8));
    expect.setSchema(testSchema);
    expectValue.setValue(RandomUtil.randomString(8));
    expectValue.setChildren(Collections.singletonList(
        new Child(RandomUtil.randomBigDecimal())
    ));
    expect.setValue(BeanUtil.beanToMap(expectValue));
    expect.setUpdateTime(Instant.now());
    repository.save(expect);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  void queryByKey() throws InvalidProtocolBufferException {
    SettingItem actual = settingBlockingStub.getSetting(GetSettingRequest.newBuilder()
        .setKey(expect.getKey())
        .build()
    );
    String actualValueJson = JsonFormat.printer().print(actual.getValue());
    TestEntity actualValue = objectMapper.readValue(actualValueJson, TestEntity.class);
    Assertions.assertEquals(expectValue.getValue(), actualValue.getValue());
    Assertions.assertEquals(expectValue.getChildren().size(), actualValue.getChildren().size());
    Assertions.assertEquals(
        expectValue.getChildren().getFirst().getChild(),
        actualValue.getChildren().getFirst().getChild()
    );
  }

  @Test
  void queryByKey_notExist() {
    GetSettingRequest request = GetSettingRequest.newBuilder()
        .setKey(RandomUtil.randomString(9))
        .build();
    Assertions.assertThrows(StatusRuntimeException.class, () ->
        settingBlockingStub.getSetting(request)
    );
  }
}
