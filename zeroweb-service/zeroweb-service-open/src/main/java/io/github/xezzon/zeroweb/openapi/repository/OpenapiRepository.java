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

package io.github.xezzon.zeroweb.openapi.repository;

import io.github.xezzon.zeroweb.openapi.Openapi;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// 对外接口数据访问仓库接口
///
/// 继承自 [JpaRepository] 和 [JpaSpecificationExecutor]，
/// 提供对 [Openapi] 实体对象的数据库操作能力。
///
/// @author xezzon
@Repository
@NullMarked
public interface OpenapiRepository extends
    JpaRepository<Openapi, String>,
    JpaSpecificationExecutor<Openapi> {

  /// 根据接口编码查询对外接口
  ///
  /// @param code 接口编码
  /// @return 包含指定编码的对外接口（如果存在）
  Optional<Openapi> findByCode(String code);
}
