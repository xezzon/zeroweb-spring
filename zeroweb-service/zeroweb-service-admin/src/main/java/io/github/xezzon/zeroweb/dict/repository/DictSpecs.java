package io.github.xezzon.zeroweb.dict.repository;

import io.github.xezzon.zeroweb.dict.Dict;
import io.github.xezzon.zeroweb.dict.Dict_;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;

/// @author xezzon
@NullMarked
public class DictSpecs {

  public static Specification<Dict> isDictTag() {
    return (root, _, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Dict_.TAG), Dict.DICT_TAG);
  }

  private DictSpecs() {
  }
}
