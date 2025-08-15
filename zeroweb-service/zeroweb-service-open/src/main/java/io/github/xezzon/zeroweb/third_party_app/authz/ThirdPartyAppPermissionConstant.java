package io.github.xezzon.zeroweb.third_party_app.authz;

import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.PermissionConstantUtil;
import java.util.List;

/**
 * @author xezzon
 */
public class ThirdPartyAppPermissionConstant {

  public static final String INVITE_MEMBER = "third-party-app:#:invite-member";
  public static final String LIST_MEMBER = "third-party-app:#:list-member";
  public static final String MOVE_OWNERSHIP = "third-party-app:#:move-ownership";
  public static final String ROLL_ACCESS_SECRET = "third-party-app:#:roll-access-secret";

  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = PermissionConstantUtil.read(ThirdPartyAppPermissionConstant.class);
  }

  private ThirdPartyAppPermissionConstant() {
  }

  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
