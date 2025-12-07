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

package io.github.xezzon.zeroweb.third_party_app.event;

import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;

/// 第三方应用创建事件
///
/// 当第三方应用创建成功后发布的事件，用于通知其他组件进行相关处理
///
/// @param thirdPartyApp 创建的第三方应用
///
/// @author xezzon
public record ThirdPartyAppCreatedEvent(ThirdPartyApp thirdPartyApp) {

}
