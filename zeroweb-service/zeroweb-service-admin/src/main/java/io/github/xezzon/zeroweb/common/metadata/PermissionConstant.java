package io.github.xezzon.zeroweb.common.metadata;

import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.metadata.MenuType;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 接口权限
 */
public final class PermissionConstant {

  static final List<MenuInfo> PERMISSIONS;

  static {
    PERMISSIONS = Arrays.stream(PermissionConstant.class.getDeclaredFields())
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
}
