/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 xezzon
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

package io.github.xezzon.zeroweb.user.entity;

import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.user.User;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author xezzon
 */
@Getter
@Setter
public class UserInfoResp {

  private String id;
  private String username;
  private String nickname;

  /**
   * 隐藏非公开的用户信息字段
   * @param user 用户信息
   * @return 用户公开展示的信息
   */
  public static UserInfoResp from(User user) {
    return Converter.INSTANCE.from(user);
  }

  @Mapper
  interface Converter extends From<User, UserInfoResp> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Override
    UserInfoResp from(User source);
  }
}
