package io.github.xezzon.zeroweb.setting;

import io.github.xezzon.zeroweb.setting.SettingGrpc.SettingBlockingStub;
import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xezzon
 */
@SpringBootTest
class SettingGrpcTest {

  @Resource
  private SettingBlockingStub settingBlockingStub;
}
