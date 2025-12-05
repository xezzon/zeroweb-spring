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

package io.github.xezzon.zeroweb.dict.repository;

import io.github.xezzon.zeroweb.dict.Dict;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// 字典数据仓库
///
/// 提供字典数据的持久化操作，继承自 [JpaRepository] 和 [JpaSpecificationExecutor]。
///
/// @author xezzon
@Repository
@NullMarked
public interface DictRepository extends
    JpaRepository<Dict, String>,
    JpaSpecificationExecutor<Dict> {

  /// 根据字典目和字典码查找字典
  ///
  /// @param tag 字典目编码
  /// @param code 字典码
  /// @return 字典实体（可能为空）
  Optional<Dict> findByTagAndCode(String tag, String code);

  /// 根据上级ID集合查找字典列表
  ///
  /// @param parentIds 上级ID集合
  /// @return 字典列表
  List<Dict> findByParentIdIn(Collection<String> parentIds);

  /// 根据字典目查找字典列表，并按排序号升序排列
  ///
  /// @param tag 字典目编码
  /// @return 字典列表（已按排序号升序排列）
  List<Dict> findByTagOrderByOrdinalAsc(String tag);
}
