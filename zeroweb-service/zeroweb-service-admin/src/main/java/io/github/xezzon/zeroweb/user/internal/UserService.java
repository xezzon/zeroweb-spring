/*
 * SPDX-FileCopyrightText: Copyright (C) 2025 xezzon
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
import io.github.xezzon.zeroweb.user.IUserService4Auth;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class UserService implements IUserService4Auth {

  private final UserRepository userRepository;

  UserService(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /// 添加用户
  ///
  /// @param user 用户
  /// @throws RepeatDataException 如果用户名已存在，则抛出此异常
  protected void addUser(User user) {
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
  protected @Nullable User getByUsername(@NonNull final String username) {
    return userRepository.findByUsername(username).orElse(null);
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
