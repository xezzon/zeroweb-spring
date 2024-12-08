package io.github.xezzon.zeroweb.user.service;

import io.github.xezzon.zeroweb.user.domain.User;

/**
 * @author xezzon
 */
public interface IUserService4Auth {

  /**
   * 根据用户名获取用户信息
   * @param username 用户名
   * @return 用户信息
   */
  User getUserByUsername(String username);
}
