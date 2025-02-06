package io.github.xezzon.zeroweb.metadata;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务信息
 * @author xezzon
 */
@Getter
@Setter
public class ServiceInfo {

  /**
   * 服务名称
   */
  private String name;
  /**
   * 服务版本
   */
  private String version;
  /**
   * 服务类型
   */
  private ServiceType type = ServiceType.SERVER;
  /**
   * 是否隐藏 站内的链接不应该包含`隐藏`的服务。
   */
  private boolean hidden = true;
}
