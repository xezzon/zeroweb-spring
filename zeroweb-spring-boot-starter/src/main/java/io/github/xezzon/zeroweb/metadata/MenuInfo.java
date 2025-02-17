package io.github.xezzon.zeroweb.metadata;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 资源信息
 */
@Getter
@Setter
@ToString
public class MenuInfo {

  /**
   * 资源类型
   */
  private MenuType type;
  /**
   * 资源路径 不同类型的资源有不同的路径格式
   * @see MenuType
   */
  private String path;
  /**
   * 访问资源所需要的权限 取并集，即资源必须满足所列出的所有权限
   */
  private Collection<String> permissions;
}
