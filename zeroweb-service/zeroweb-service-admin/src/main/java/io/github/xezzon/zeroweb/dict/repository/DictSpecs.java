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
import io.github.xezzon.zeroweb.dict.Dict_;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;

/// 字典查询规范
///
/// 提供 JPA Specification 查询规范，用于构建复杂的查询条件。
///
/// @author xezzon
@NullMarked
public class DictSpecs {

  /// 创建字典目查询规范
  ///
  /// 用于查询字典目（即 tag = [Dict#DICT_TAG] 的记录）。
  ///
  /// @return 查询规范
  public static Specification<Dict> isDictTag() {
    return (root, _, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Dict_.TAG), Dict.DICT_TAG);
  }

  /// 私有构造函数，防止实例化
  private DictSpecs() {
  }
}
