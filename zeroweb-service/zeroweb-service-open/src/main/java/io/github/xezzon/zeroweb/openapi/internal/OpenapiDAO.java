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

package io.github.xezzon.zeroweb.openapi.internal;

import io.github.xezzon.zeroweb.common.jpa.BaseDAO;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.openapi.Openapi_;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

/// 对外接口数据访问对象
///
/// 封装了对 [Openapi] 实体对象的数据库操作，
/// 继承自 [BaseDAO]，提供基础的增删改查功能。
/// 同时提供了查询已发布接口的专用方法。
///
/// @author xezzon
@Repository
@NullMarked
public class OpenapiDAO extends BaseDAO<Openapi, String, OpenapiRepository> {

  /// 依赖注入
  /// @param repository 对外接口 JPA 接口
  OpenapiDAO(OpenapiRepository repository) {
    super(repository, Openapi.class);
  }

  @Override
  public ICopier<Openapi> getCopier() {
    return Copier.INSTANCE;
  }

  @Override
  public Page<Openapi> findAll(ODataQueryOption odata) {
    Sort sort = Sort.by(Order.asc(Openapi_.CODE));
    return super.findAll(odata, null, sort);
  }

  /// 查询已发布的对外接口列表。按编码升序排序。
  ///
  /// @param odata OData查询参数，用于指定查询条件、排序方式、分页信息等
  /// @return 符合查询条件的已发布对外接口分页结果
  public Page<Openapi> listPublishedOpenapi(ODataQueryOption odata) {
    Specification<Openapi> spec = (root, _, cb) ->
        cb.equal(root.get(Openapi_.status), OpenapiStatus.PUBLISHED);
    Sort sort = Sort.by(Order.asc(Openapi_.CODE));
    return super.findAll(odata, spec, sort);
  }

  @Mapper
  interface Copier extends ICopier<Openapi> {

    Copier INSTANCE = Mappers.getMapper(Copier.class);
  }
}
