package io.github.xezzon.zeroweb.common.jpa;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author xezzon
 */
public class BaseSpecs {

  public static <T> Specification<T> identicallyEqual() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.and();
  }

  public static <T> Specification<T> identicallyNotEqual() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.or();
  }

  private BaseSpecs() {
  }
}
