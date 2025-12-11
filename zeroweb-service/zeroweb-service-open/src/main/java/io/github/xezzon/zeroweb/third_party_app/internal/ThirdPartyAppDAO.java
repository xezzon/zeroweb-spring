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

package io.github.xezzon.zeroweb.third_party_app.internal;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.third_party_app.ThirdPartyApp;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppRepository;
import io.github.xezzon.zeroweb.third_party_app.repository.ThirdPartyAppSpec;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/// 第三方应用数据访问对象
///
/// 封装对第三方应用实体的数据库操作，提供基础的增删改查和分页查询功能
///
/// @author xezzon
@Repository
@NullMarked
public class ThirdPartyAppDAO extends BaseDAO<ThirdPartyApp, String, ThirdPartyAppRepository> {

  /// 依赖注入
  /// @param repository 第三方应用 JPA 接口
  protected ThirdPartyAppDAO(ThirdPartyAppRepository repository) {
    super(repository, ThirdPartyApp.class);
  }

  @Override
  public ICopier<ThirdPartyApp> getCopier() {
    return Copier.INSTANCE;
  }

  @Override
  public Page<ThirdPartyApp> findAll(final ODataQueryOption odata) {
    Sort sort = ThirdPartyAppSpec.defaultSort();
    return this.findAll(odata, null, sort);
  }

  @Mapper
  interface Copier extends ICopier<ThirdPartyApp> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
