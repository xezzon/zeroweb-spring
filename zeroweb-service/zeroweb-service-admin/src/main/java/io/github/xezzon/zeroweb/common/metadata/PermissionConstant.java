package io.github.xezzon.zeroweb.common.metadata;

import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.PermissionConstantUtil;
import java.util.List;

/// 接口权限
public final class PermissionConstant {

  public static final String APP_WRITE = "app:write";
  public static final String AUTHZ_READ = "authz:read";
  public static final String AUTHZ_ROLE_USER = "authz:role-user";
  public static final String AUTHZ_ROLE_PERMISSION = "authz:role-permission";
  public static final String DICT_READ = "dict:read";
  public static final String DICT_WRITE = "dict:write";
  public static final String ROLE_READ = "role:read";
  public static final String ROLE_WRITE = "role:write";
  public static final String SETTING_WRITE = "setting:write";

  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = PermissionConstantUtil.read(PermissionConstant.class);
  }

  private PermissionConstant() {
  }

  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
