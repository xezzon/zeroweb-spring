package io.github.xezzon.zeroweb.metadata;

import io.github.xezzon.zeroweb.common.exception.ZerowebRuntimeException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author xezzon
 */
public final class PermissionConstantUtil {

  public static List<MenuInfo> read(Class<?> constant) {
    return Arrays.stream(constant.getDeclaredFields())
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

  private PermissionConstantUtil() {
  }
}
