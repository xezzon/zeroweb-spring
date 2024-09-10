package io.github.xezzon.geom.common.jpa;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author xezzon
 */
public class BaseSpecs {

  public static <T> Specification<T> TRUE() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.and();
  }

  public static <T> Specification<T> FALSE() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.or();
  }

  private BaseSpecs() {
  }
}
