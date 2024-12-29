package io.github.xezzon.zeroweb.common.jpa;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author xezzon
 */
public class BaseSpecs {

  /**
   * 构造`WHERE TRUE;`的 JPA Specification。
   * @param <T> 目标类型
   * @return 永远为 true 的 Specification
   */
  public static <T> Specification<T> identicallyEqual() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.and();
  }

  /**
   * 构造`WHERE FALSE;`的 JPA Specification。
   * @param <T> 目标类型
   * @return 永远为 false 的 Specification
   */
  public static <T> Specification<T> identicallyNotEqual() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.or();
  }

  private BaseSpecs() {
  }
}
