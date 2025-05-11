package io.github.xezzon.zeroweb.user.service;

import io.github.xezzon.zeroweb.user.domain.User;
import java.util.Collection;
import java.util.List;

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

  /**
   * 根据ID批量查询用户
   * @param userIds 用户ID集合
   * @return 用户列表
   */
  List<User> findByIdIn(Collection<String> userIds);
}
