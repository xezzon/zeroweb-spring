package io.github.xezzon.zeroweb.metadata;

/**
 * 菜单类型
 */
public enum MenuType {

  /**
   * 路由 路径格式为 `/menu/submenu`
   */
  ROUTE,
  /**
   * 外部链接。点击后会打开一个新的标签页 路径格式为 `https://domain.com/path`
   */
  EXTERNAL_LINK,
  /**
   * 嵌入页面。会在当前页面嵌入一个外部网页。 路径格式为 `https://domain.com/path`
   */
  EMBEDDED,
  /**
   * 接口权限 路径格式为 `resource:operation`，operation 通常为 `read`（可省略）、`write` 等。
   */
  PERMISSION,
  /**
   * 资源权限 路径格式为 `resource:#:operation`，operation 通常为 `read`（可省略）、`write` 等。
   */
  GROUP_PERMISSION,
}
