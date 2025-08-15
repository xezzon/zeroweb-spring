package io.github.xezzon.zeroweb.common.metadata;

import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.PermissionConstantUtil;
import java.util.List;

/**
 * @author xezzon
 */
public class PermissionConstant {

  public static final String OPENAPI_WRITE = "openapi:write";
  public static final String OPENAPI_PUBLISH = "openapi:publish";
  public static final String SUBSCRIPTION_AUDIT = "subscription:audit";
  public static final String THIRD_PARTY_APP_READ = "third-party-app:read";

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
