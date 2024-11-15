package io.github.xezzon.geom.openapi.service;

import io.github.xezzon.geom.openapi.domain.Openapi;
import org.jetbrains.annotations.Nullable;

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
}
