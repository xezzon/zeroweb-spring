package io.github.xezzon.zeroweb.metadata;

import java.util.List;

/**
 * 权限服务
 */
public interface IMenuService {

  /**
   * 列举服务内所有的接口权限与资源权限
   * @return 权限列表
   */
  List<MenuInfo> list();
}
