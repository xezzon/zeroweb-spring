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

package io.github.xezzon.zeroweb.app.internal;

import io.github.xezzon.zeroweb.app.App;
import io.github.xezzon.zeroweb.app.repository.AppRepository;
import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Repository;

/// `AppDAO` 是服务实体的数据库访问对象（DAO）。
///
/// 它提供了对 [App] 实体的基本 CRUD 操作，并集成了 MapStruct 复制器。
///
/// @author xezzon
@Repository
@NullMarked
public class AppDAO extends BaseDAO<App, String, AppRepository> {

  /// 构造函数，注入 [AppRepository]。
  ///
  /// @param repository [AppRepository] 实例。
  AppDAO(final AppRepository repository) {
    super(repository, App.class);
  }

  @Override
  public ICopier<App> getCopier() {
    return Copier.INSTANCE;
  }

  @Mapper
  interface Copier extends ICopier<App> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
