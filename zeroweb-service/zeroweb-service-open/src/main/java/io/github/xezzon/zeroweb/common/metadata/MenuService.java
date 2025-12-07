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

package io.github.xezzon.zeroweb.common.metadata;

import io.github.xezzon.zeroweb.metadata.IMenuService;
import io.github.xezzon.zeroweb.metadata.MenuInfo;
import io.github.xezzon.zeroweb.subscription.authz.SubscriptionPermissionConstant;
import io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/// 菜单服务实现
/// 
/// 提供菜单信息的聚合查询功能，整合来自多个权限源的菜单信息。
/// @author xezzon
@SuppressWarnings("unused")
@Service
public class MenuService implements IMenuService {

  /// 查询所有菜单信息
  /// 
  /// @return 菜单信息列表
  @Override
  public List<MenuInfo> list() {
    return Stream.of(
            PermissionConstant.getPermissions(),
            ThirdPartyAppPermissionConstant.getPermissions(),
            SubscriptionPermissionConstant.getPermissions()
        )
        .flatMap(Collection::stream)
        .toList();
  }
}
