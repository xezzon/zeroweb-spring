/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This file is part of ZeroWeb.
 *
 * ZeroWeb is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * ZeroWeb is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with ZeroWeb. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xezzon.zeroweb.user.internal;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.user.IUserService4Auth;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/// 用户服务
/// @author xezzon
@Service
public class UserService implements IUserService4Auth {

  /// 用户 JPA 接口
  private final UserRepository userRepository;
  /// 用户数据库操作
  private final UserDAO userDAO;

  /// 依赖注入
  ///
  /// @param userRepository 用户 JPA 接口
  /// @param userDAO 用户数据库操作
  UserService(final UserRepository userRepository, final UserDAO userDAO) {
    this.userRepository = userRepository;
    this.userDAO = userDAO;
  }

  /// 添加用户
  ///
  /// @param user 用户
  /// @throws RepeatDataException 如果用户名已存在，则抛出此异常
  @Transactional()
  void addUser(User user) {
    /* 前置校验 */
    Optional<User> exist = userRepository.findByUsername(user.getUsername());
    if (exist.isPresent()) {
      throw new RepeatDataException("`" + user.getUsername() + "`");
    }
    /* 持久化 */
    userRepository.save(user);
  }

  /// 根据用户名获取用户信息
  ///
  /// @param username 用户名
  /// @return 返回与用户名对应的用户信息，若不存在则返回null
  @Nullable User getByUsername(@NonNull final String username) {
    return userRepository.findByUsername(username).orElse(null);
  }

  /// 使用 OData 参数进行分页查询用户
  /// @param odata OData 查询选项，包含查询条件、排序、分页等参数
  /// @return 分页结果，包含用户列表和分页信息
  Page<User> listAll(final ODataQueryOption odata) {
    return userDAO.findAll(odata);
  }

  @Override
  public @Nullable User getUserByUsername(final String username) {
    return this.getByUsername(username);
  }

  @Override
  public List<User> findByIdIn(final Collection<String> userIds) {
    return userRepository.findAllById(userIds);
  }
}
