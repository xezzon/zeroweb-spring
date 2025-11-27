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

package io.github.xezzon.zeroweb.auth;

import cn.dev33.satoken.stp.StpInterface;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @author xezzon
 */
@Component
@SuppressWarnings("unused")
public class JwtStpInterface implements StpInterface {

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return JwtAuth.get()
        .map(JwtClaim::getEntitlementsList)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    return JwtAuth.get()
        .map(JwtClaim::getRolesList)
        .map(Collections::unmodifiableList)
        .orElse(Collections.emptyList());
  }
}
