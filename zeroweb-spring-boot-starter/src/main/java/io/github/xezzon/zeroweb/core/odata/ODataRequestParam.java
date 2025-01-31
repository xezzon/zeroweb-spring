package io.github.xezzon.zeroweb.core.odata;

import io.github.xezzon.tao.trait.Into;

/**
 * @param top 最大返回条数
 * @param skip 偏移量
 * @author xezzon
 */
public record ODataRequestParam(
    Integer top,
    Integer skip
) implements Into<ODataQueryOption> {

  @Override
  public ODataQueryOption into() {
    return ODataQueryOption.builder()
        .top(this.top)
        .skip(this.skip)
        .build();
  }
}
