package io.github.xezzon.zeroweb.metadata;

import cn.hutool.core.util.RandomUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
class PermissionConstantUtilTest {

  @Test
  void read() {
    List<MenuInfo> menuInfos = PermissionConstantUtil.read(PermissionConstant.class);
    Assertions.assertTrue(menuInfos.stream().anyMatch(menuInfo ->
        Objects.equals(menuInfo.getPath(), PermissionConstant.PERMISSION1)
    ));
  }
}

class PermissionConstant {

  public static final String PERMISSION1 = RandomUtil.randomString(8);
  public static final String PERMISSION2 = RandomUtil.randomString(8);
  public static final String PERMISSION3 = RandomUtil.randomString(8);
  public static final String PERMISSION4 = RandomUtil.randomString(8);
}
