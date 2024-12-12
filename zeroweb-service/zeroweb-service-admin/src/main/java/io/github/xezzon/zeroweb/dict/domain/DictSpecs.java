package io.github.xezzon.zeroweb.dict.domain;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author xezzon
 */
public class DictSpecs {

  public static Specification<Dict> isDictTag() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Dict_.TAG), Dict.DICT_TAG);
  }

  private DictSpecs() {
  }
}
