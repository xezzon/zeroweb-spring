package io.github.xezzon.zeroweb.subscription.authz;

import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.PermissionConstantUtil;
import java.util.List;

/// @author xezzon
public class SubscriptionPermissionConstant {

  public static final String SUBSCRIBE = "subscription:#:add";
  public static final String LIST_SUBSCRIPTION = "subscription:#:read";

  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = PermissionConstantUtil.read(SubscriptionPermissionConstant.class);
  }

  private SubscriptionPermissionConstant() {
  }

  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
