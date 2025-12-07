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

package io.github.xezzon.zeroweb.setting.repository;

import io.github.xezzon.zeroweb.setting.Setting;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// 对 [业务参数][Setting] 进行数据库操作的 JPA 接口
///
/// 提供业务参数实体的数据库访问接口，继承自Spring Data JPA的JpaRepository和JpaSpecificationExecutor。
/// 支持基础的CRUD操作和复杂的规格查询，支持OData协议。
/// @author xezzon
@Repository
@NullMarked
public interface SettingRepository extends
    JpaRepository<Setting, String>,
    JpaSpecificationExecutor<Setting> {

  /// 根据业务参数标识查询参数
  ///
  /// 通过参数的唯一标识符code查询对应的配置项。
  /// 常用于根据参数标识快速获取参数配置的场景。
  /// @param code 业务参数标识，如 `system.theme`
  /// @return 业务参数
  Optional<Setting> findByCode(String code);
}
