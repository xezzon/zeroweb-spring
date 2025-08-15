package io.github.xezzon.zeroweb.third_party_app.authz;

import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.MenuType;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author xezzon
 */
public class PermissionConstant {

  public static final String INVITE_MEMBER = "third-party-app:#:invite-member";
  public static final String LIST_MEMBER = "third-party-app:#:list-member";
  public static final String MOVE_OWNERSHIP = "third-party-app:#:move-ownership";
  public static final String ROLL_ACCESS_SECRET = "third-party-app:#:roll-access-secret";

  private static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = Arrays.stream(
            io.github.xezzon.zeroweb.common.metadata.PermissionConstant.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> Modifier.isPublic(field.getModifiers()))
        .map(field -> {
          final String value;
          try {
            value = field.get(null).toString();
          } catch (IllegalAccessException e) {
            throw new ZerowebRuntimeException(e);
          }
          final MenuInfo resourceInfo = new MenuInfo();
          resourceInfo.setType(MenuType.PERMISSION);
          resourceInfo.setPath(value);
          resourceInfo.setPermissions(Collections.singleton(value));
          return resourceInfo;
        })
        .toList();
  }

  private PermissionConstant() {
  }

  public static List<MenuInfo> getPermissions() {
    return PERMISSIONS;
  }
}
