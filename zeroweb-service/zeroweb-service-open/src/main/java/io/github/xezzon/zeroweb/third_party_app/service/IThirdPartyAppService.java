package io.github.xezzon.zeroweb.third_party_app.service;

import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;

/**
 * @author xezzon
 */
public interface IThirdPartyAppService {

  /**
   * 检查当前人员是否有指定应用的权限
   * @param appId 应用ID
   * @throws DataPermissionForbiddenException 只有应用所有者有权限访问
   */
  void checkPermission(String appId) throws DataPermissionForbiddenException;
}
