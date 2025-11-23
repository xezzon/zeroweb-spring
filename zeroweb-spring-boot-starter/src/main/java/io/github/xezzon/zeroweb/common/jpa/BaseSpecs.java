package io.github.xezzon.zeroweb.common.jpa;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author xezzon
 */
@NullMarked
public class BaseSpecs {

  /**
   * 构造`WHERE TRUE;`的 JPA Specification。
   * @param <T> 目标类型
   * @return 永远为 true 的 Specification
   */
  public static <T> Specification<T> identicallyEqual() {
    return (_, _, criteriaBuilder) -> criteriaBuilder.and();
  }

  /**
   * 构造`WHERE FALSE;`的 JPA Specification。
   * @param <T> 目标类型
   * @return 永远为 false 的 Specification
   */
  public static <T> Specification<T> identicallyNotEqual() {
    return (_, _, criteriaBuilder) -> criteriaBuilder.or();
  }

  private BaseSpecs() {
  }
}
