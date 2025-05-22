package io.github.xezzon.zeroweb.common.redis;

import io.github.xezzon.zeroweb.common.jpa.TestEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author xezzon
 */
@Getter
@Setter
@ToString
public class ChildEntity extends TestEntity {

  private String field3;
}
