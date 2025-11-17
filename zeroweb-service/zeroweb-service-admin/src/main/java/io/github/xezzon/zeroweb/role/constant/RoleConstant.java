package io.github.xezzon.zeroweb.role.constant;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.role.Role;

/**
 * @author xezzon
 */
public class RoleConstant {

  public static final Role ROOT = new Role();

  static {
    ROOT.setId("3");
    ROOT.setCode("ROOT");
    ROOT.setValue("ROOT");
    ROOT.setName("超级管理员");
    ROOT.setInheritable(false);
    ROOT.setParentId(DatabaseConstant.ROOT_ID);
  }

  private RoleConstant() {
  }
}
