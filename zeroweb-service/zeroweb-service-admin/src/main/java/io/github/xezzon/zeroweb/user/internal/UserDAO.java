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

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.user.User;
import io.github.xezzon.zeroweb.user.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/**
 * @author xezzon
 */
@Repository
public class UserDAO extends BaseDAO<User, String, UserRepository> {

  /// 依赖注入
  /// @param repository 用户 JPA 接口
  protected UserDAO(final UserRepository repository) {
    super(repository, User.class);
  }

  @Override
  public ICopier<User> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<User> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
