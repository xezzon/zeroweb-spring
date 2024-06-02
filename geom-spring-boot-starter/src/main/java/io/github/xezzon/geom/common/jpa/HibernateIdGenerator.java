package io.github.xezzon.geom.common.jpa;

import io.github.xezzon.geom.common.context.ApplicationContextProvider;
import io.github.xezzon.tao.data.IdGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

/**
 * ID 生成器
 * @author xezzon
 */
@Component
public class HibernateIdGenerator implements IdentifierGenerator {

  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o
  ) {
    Object originId = sharedSessionContractImplementor
        .getEntityPersister(null, o)
        .getIdentifier(o, sharedSessionContractImplementor);
    if (originId != null) {
      return originId;
    }
    IdGenerator idGenerator = ApplicationContextProvider
        .getApplicationContext()
        .getBean(IdGenerator.class);
    return idGenerator.nextId();
  }
}
