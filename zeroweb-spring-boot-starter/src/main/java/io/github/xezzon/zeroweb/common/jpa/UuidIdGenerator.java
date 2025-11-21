package io.github.xezzon.zeroweb.common.jpa;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.core.trait.IdGenerator;
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

  private static final NoArgGenerator UUID_GENERATOR = Generators.timeBasedEpochRandomGenerator();

  @Override
  public String nextId() {
    return UUID_GENERATOR.generate().toString();
  }
}
