package io.github.xezzon.zeroweb.common.jpa;

import io.github.xezzon.tao.data.IdGenerator;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * ID生成器（UUID策略）
 * @author xezzon
 */
@Component
@ConditionalOnProperty(
    prefix = ZerowebConfig.ZEROWEB,
    name = ZerowebConfig.ID_GENERATOR,
    havingValue = "UUID",
    matchIfMissing = true
)
public class UuidIdGenerator implements IdGenerator {

  @Override
  public String nextId() {
    return UUID.randomUUID().toString();
  }
}
