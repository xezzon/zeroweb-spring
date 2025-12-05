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

package io.github.xezzon.zeroweb.locale.repository;

import io.github.xezzon.zeroweb.locale.Language;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// 用于语言的数据库操作。
///
/// @author xezzon
@Repository
@NullMarked
public interface LanguageRepository extends
    JpaRepository<Language, String>,
    JpaSpecificationExecutor<Language> {

  /// 根据字典标签和语言标签查找语言。
  ///
  /// @param dictTag 字典标签。
  /// @param languageTag 语言标签。
  /// @return 包含语言的 Optional 对象。
  Optional<Language> findByDictTagAndLanguageTag(String dictTag, String languageTag);

  /// 根据字典标签查询语言列表，并按排序字段升序排列。
  ///
  /// @param dictTag 字典标签。
  /// @return 语言列表。
  List<Language> findByDictTagOrderByOrdinalAsc(String dictTag);
}
