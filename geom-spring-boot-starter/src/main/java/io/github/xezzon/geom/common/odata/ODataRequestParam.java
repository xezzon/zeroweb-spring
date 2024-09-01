package io.github.xezzon.geom.common.odata;

import io.github.xezzon.tao.trait.Into;

/**
 * @author xezzon
 */
public record ODataRequestParam(
    Integer $top,
    Integer $skip
) implements Into<ODataQueryOption> {

  @Override
  public ODataQueryOption into() {
    return ODataQueryOption.builder()
        .top(this.$top)
        .skip(this.$skip)
        .build();
  }
}
