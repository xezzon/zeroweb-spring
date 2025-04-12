package io.github.xezzon.zeroweb.auth.util;

import cn.dev33.satoken.stp.StpUtil;
import io.github.xezzon.zeroweb.user.domain.User;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xezzon
 */
public class SessionUtil {

  public static final String USER = "user";
  public static final String ROLE = "roles";
  public static final String PERMISSION = "permissions";

  private SessionUtil() {
  }

  public static void saveUser(User user) {
    StpUtil.getSession().set(USER, user);
  }

  public static User loadUser() {
    return StpUtil.getSession().getModel(USER, User.class);
  }

  public static void saveRoles(Set<String> roles) {
    StpUtil.getSession().set(ROLE, new RoleSet(roles));
  }

  public static Set<String> loadRoles() {
    return StpUtil.getSession().getModel(ROLE, RoleSet.class);
  }

  public static void savePermissions(Set<String> permissions) {
    StpUtil.getSession().set(PERMISSION, new PermissionSet(permissions));
  }

  public static Set<String> loadPermissions() {
    return StpUtil.getSession().getModel(PERMISSION, PermissionSet.class);
  }
}

class RoleSet extends HashSet<String> {

  public RoleSet(Set<String> roles) {
    super(roles);
  }
}

class PermissionSet extends HashSet<String> {

  public PermissionSet(Set<String> permissions) {
    super(permissions);
  }
}
