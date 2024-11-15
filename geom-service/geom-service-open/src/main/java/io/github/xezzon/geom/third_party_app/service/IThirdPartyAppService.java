package io.github.xezzon.geom.third_party_app.service;

/**
 * @author xezzon
 */
public interface IThirdPartyAppService {

  /**
   * 检查当前人员是否有指定应用的权限
   * @param appId 应用ID
   */
  void checkPermission(String appId);
}
