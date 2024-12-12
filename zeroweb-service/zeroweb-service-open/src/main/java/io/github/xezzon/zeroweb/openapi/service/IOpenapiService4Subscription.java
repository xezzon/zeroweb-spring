package io.github.xezzon.zeroweb.openapi.service;

import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.domain.Openapi;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;

/**
 * @author xezzon
 */
public interface IOpenapiService4Subscription {

  /**
   * 根据编码查询对外接口
   * @param openapiCode 接口编码
   * @return 对外接口编码
   */
  @Nullable
  Openapi getByCode(String openapiCode);

  /**
   * 查询符合条件的OpenAPI列表
   * @param odata OData查询参数，用于指定查询条件、排序方式、分页信息等
   * @return 符合查询条件的OpenAPI分页结果
   */
  Page<Openapi> listPublishedOpenapi(ODataQueryOption odata);
}
