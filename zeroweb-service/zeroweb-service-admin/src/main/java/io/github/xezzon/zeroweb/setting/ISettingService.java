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

package io.github.xezzon.zeroweb.setting;

import java.util.NoSuchElementException;
import org.jspecify.annotations.NonNull;

/**
 * 向其他服务提供的业务参数接口
 * @author xezzon
 */
public interface ISettingService {

  /// 根据参数标识查询配置项
  ///
  /// 通过业务参数的唯一标识符查询具体的参数配置。
  /// @param code 业务参数标识，如 "system.theme"
  /// @return 查找到的配置项
  /// @throws NoSuchElementException 当参数标识不存在时抛出
  Setting queryByCode(@NonNull String code);
}
