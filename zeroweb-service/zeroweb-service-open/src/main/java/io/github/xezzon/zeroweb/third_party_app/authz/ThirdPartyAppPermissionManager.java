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

package io.github.xezzon.zeroweb.third_party_app.authz;

import static io.github.xezzon.zeroweb.third_party_app.authz.ThirdPartyAppPermissionConstant.LIST_MEMBER;

import io.github.xezzon.zeroweb.common.exception.DataPermissionForbiddenException;
import io.github.xezzon.zeroweb.third_party_app.IThirdPartyAppMemberService;
import io.github.xezzon.zeroweb.third_party_app.authn.ThirdPartyAppMember;
import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

/// @author xezzon
@Component
public class ThirdPartyAppPermissionManager {

  private final IThirdPartyAppMemberService thirdPartyAppMemberService;

  public ThirdPartyAppPermissionManager(IThirdPartyAppMemberService thirdPartyAppMemberService) {
    this.thirdPartyAppMemberService = thirdPartyAppMemberService;
  }

  public void check(String groupId, String userId, String permission) {
    Supplier<DataPermissionForbiddenException> thr = () ->
        new DataPermissionForbiddenException(groupId, userId, permission);
    ThirdPartyAppMember member = thirdPartyAppMemberService
        .queryMember(groupId, userId)
        .orElseThrow(thr);
    if (member.isOwner()) {
      // 所有者拥有该资源的所有权限
      return;
    }
    if (!Objects.equals(LIST_MEMBER, permission)) {
      throw thr.get();
    }
  }
}
