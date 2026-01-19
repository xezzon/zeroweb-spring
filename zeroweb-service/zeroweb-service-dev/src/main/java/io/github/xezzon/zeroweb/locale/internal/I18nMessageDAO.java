/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
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

package io.github.xezzon.zeroweb.locale.internal;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.locale.I18nMessage;
import io.github.xezzon.zeroweb.locale.I18nMessage_;
import io.github.xezzon.zeroweb.locale.repository.I18nMessageRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

/// 国际化内容数据访问对象。
///
/// @author xezzon
@Repository
@NullMarked
public class I18nMessageDAO extends BaseDAO<I18nMessage, String, I18nMessageRepository> {

  /// 依赖注入
  /// @param repository 国际化内容 JPA 接口
  I18nMessageDAO(final I18nMessageRepository repository) {
    super(repository, I18nMessage.class);
  }

  /// 根据命名空间查询所有国际化内容。
  ///
  /// @param namespace 国际化内容命名空间。
  /// @param odata OData 查询选项。
  /// @return 包含国际化内容的页面。
  Page<I18nMessage> findAllWithNamespace(final String namespace, final ODataQueryOption odata) {
    final Specification<I18nMessage> spec = (root, _, cb) ->
        cb.equal(root.get(I18nMessage_.namespace), namespace);
    final Sort sort = Sort.by(Order.asc(I18nMessage_.MESSAGE_KEY));
    return super.findAll(odata, spec, sort);
  }
}
