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

import io.github.xezzon.zeroweb.locale.I18nMessage;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/// 国际化内容仓库接口。
/// 用于国际化内容的数据库操作。
///
/// @author xezzon
@Repository
@NullMarked
public interface I18nMessageRepository extends
    JpaRepository<I18nMessage, String>,
    JpaSpecificationExecutor<I18nMessage> {

  /// 根据命名空间和消息键查找国际化内容。
  ///
  /// @param namespace 国际化内容命名空间。
  /// @param messageKey 国际化内容消息键。
  /// @return 包含国际化内容的 Optional 对象。
  Optional<I18nMessage> findByNamespaceAndMessageKey(String namespace, String messageKey);

  /// 查找所有不同的国际化内容命名空间。
  ///
  /// @return 国际化内容命名空间列表。
  @Query("SELECT DISTINCT namespace FROM I18nMessage")
  List<String> findDistinctNamespace();
}
