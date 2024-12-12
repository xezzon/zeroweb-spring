package io.github.xezzon.zeroweb.common.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xezzon
 */
@Setter(AccessLevel.PACKAGE)
@Getter
public class PagedModel<T> {

  private List<T> content;
  private PageMetadata page;

  @Setter(AccessLevel.PACKAGE)
  @Getter
  public static class PageMetadata {

    private long size;
    private long number;
    private long totalElements;
    private long totalPages;
  }
}
