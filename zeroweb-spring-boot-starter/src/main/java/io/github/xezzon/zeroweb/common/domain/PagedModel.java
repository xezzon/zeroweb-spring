package io.github.xezzon.zeroweb.common.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 分页数据
 * @author xezzon
 */
@Setter(AccessLevel.PACKAGE)
@Getter
public class PagedModel<T> {

  /**
   * 数据内容
   */
  private List<T> content;
  /**
   * 分页信息
   */
  private PageMetadata page;

  /**
   * 分页信息
   */
  @Setter(AccessLevel.PACKAGE)
  @Getter
  public static class PageMetadata {

    /**
     * 分页大写
     */
    private long size;
    /**
     * 页码
     */
    private long number;
    /**
     * 总数据量
     */
    private long totalElements;
    /**
     * 总页数
     */
    private long totalPages;
  }
}
