package io.github.xezzon.zeroweb.user.constant;

import io.github.xezzon.zeroweb.user.User;

/**
 * @author xezzon
 */
public class UserConstant {

  public static final User ROOT = new User();

  static {
    ROOT.setUsername("root");
    ROOT.setNickname("超级管理员");
  }

  private UserConstant() {
  }
}
