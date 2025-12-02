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

package io.github.xezzon.zeroweb.third_party_app;

import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMember;
import java.util.Optional;

/// @author xezzon
public interface IThirdPartyAppMemberService {

  /// 查询指定用户在指定用户组的成员身份
  ///
  /// @param groupId 用户组ID
  /// @param userId 用户ID
  /// @return 用户组成员
  Optional<ThirdPartyAppMember> queryMember(String groupId, String userId);
}
