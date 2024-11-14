package io.github.xezzon.geom.openapi.service;

import io.github.xezzon.geom.core.odata.ODataQueryOption;
import io.github.xezzon.geom.openapi.domain.Openapi;
import org.springframework.data.domain.Page;

/**
 * @author xezzon
 */
public interface IOpenapiService4ThirdPartApp {

  /**
   * 查询符合条件的OpenAPI列表
   * @param odata OData查询参数，用于指定查询条件、排序方式、分页信息等
   * @return 符合查询条件的OpenAPI分页结果
   */
  Page<Openapi> listPublishedOpenapi(ODataQueryOption odata);
}
