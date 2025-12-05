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

package io.github.xezzon.zeroweb.common.jpa;

import io.github.xezzon.zeroweb.common.context.ApplicationContextProvider;
import io.github.xezzon.zeroweb.core.trait.IdGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

/// Hibernate ID 生成器。
///
/// @author xezzon
@Component
public class HibernateIdGenerator implements IdentifierGenerator {

  /// 生成实体 ID。
  ///
  /// 如果实体已经有 ID，则返回该 ID。
  /// 否则，委托给配置的 [IdGenerator] 实现来生成一个新的 ID。
  ///
  /// @param sharedSessionContractImplementor Hibernate 会话契约实现器
  /// @param o 待生成 ID 的实体对象
  /// @return 生成的 ID
  @Override
  public Object generate(
      SharedSessionContractImplementor sharedSessionContractImplementor, Object o
  ) {
    Object originId = sharedSessionContractImplementor
        .getEntityPersister(null, o)
        .getIdentifier(o, sharedSessionContractImplementor);
    if (originId != null) {
      return originId;
    }
    IdGenerator idGenerator = ApplicationContextProvider.get().getBean(IdGenerator.class);
    return idGenerator.nextId();
  }
}
