package io.github.xezzon.geom.core.odata;

import lombok.Builder;
import lombok.Getter;

/**
 * @author xezzon
 * @see <a
 * href="https://docs.oasis-open.org/odata/odata/v4.01/cs01/abnf/odata-abnf-construction-rules.txt">OData
 * ABNF Construction Rules Version 4.01</a>
 */
@Builder()
public class ODataQueryOption {

  /**
   * @see <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#_Toc31360956">Common
   * Expression Syntax</a>
   */
  private final String filter;
  /**
   * <a href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionorderby">System Query Option $orderby</a>
   */
  private final String orderby;
  /**
   * <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionselect">System
   * Query Option $select</a>
   */
  private final String select;
  /**
   * <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionstopandskip">System
   * Query Options $top and $skip</a>
   */
  @Getter
  private final Integer top;
  /**
   * <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionstopandskip">System
   * Query Options $top and $skip</a>
   */
  private final Integer skip;
  /**
   * <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#_Toc31361044">System
   * Query Option $search</a>
   */
  private final String search;

  public Integer getPageNumber() {
    if (skip == null) {
      return 0;
    }
    return this.skip / this.top;
  }
}
