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

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.openapi.IOpenapiService4Subscription;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.openapi.enumeration.OpenapiStatus;
import io.github.xezzon.zeroweb.openapi.exception.PublishedOpenapiCannotBeDeleteException;
import io.github.xezzon.zeroweb.openapi.exception.PublishedOpenapiCannotBeModifyException;
import io.github.xezzon.zeroweb.openapi.repository.OpenapiRepository;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/// 对外接口服务类
///
/// 实现了对外接口的完整生命周期管理，包括新增、修改、发布和查询等功能。
/// 该服务类实现了 [IOpenapiService4Subscription] 接口，
/// 为订阅服务提供了获取已发布接口列表和按编码查询接口的方法。
///
/// @author xezzon
@Service
public class OpenapiService implements IOpenapiService4Subscription {

  private final OpenapiRepository openapiRepository;
  private final OpenapiDAO openapiDAO;

  /// 依赖注入
  /// @param openapiRepository 对外接口数据库管理
  /// @param openapiDAO 对外接口数据库管理
  public OpenapiService(final OpenapiRepository openapiRepository, final OpenapiDAO openapiDAO) {
    this.openapiRepository = openapiRepository;
    this.openapiDAO = openapiDAO;
  }

  /// 查询指定的对外接口
  /// @param id 对外接口 ID
  Openapi queryById(final String id) {
    return openapiRepository.findById(id).orElseThrow();
  }

  /// 添加一个新的对外接口对象到数据库
  ///
  /// @param openapi 要添加的对外接口对象
  /// @throws RepeatDataException 如果要添加的对外接口编码重复，则抛出异常
  void addOpenapi(Openapi openapi) {
    this.checkRepeat(openapi);
    openapiRepository.save(openapi);
  }

  /// 根据OData查询选项分页查询对外接口列表
  ///
  /// @param odata OData查询选项
  /// @return 分页查询结果，包含符合条件的对外接口列表
  Page<@NonNull Openapi> pageList(ODataQueryOption odata) {
    return openapiDAO.findAll(odata);
  }

  /// 修改指定的对外接口对象
  ///
  /// @param oldValue 更新前的对外接口对象
  /// @param newValue 需要修改的对外接口对象
  /// @throws RepeatDataException 如果要修改的对外接口编码重复，则抛出异常
  /// @throws PublishedOpenapiCannotBeModifyException 如果要修改的Openapi已经发布且编码（即对外的路径）被修改，则抛出异常
  void modifyOpenapi(final Openapi oldValue, final Openapi newValue) {
    this.checkRepeat(newValue);
    if (oldValue.isPublished()
        && newValue.getCode() != null
        && !Objects.equals(oldValue.getCode(), newValue.getCode())
    ) {
      // 已发布的接口不能修改编码（即对外的路径）
      throw new PublishedOpenapiCannotBeModifyException();
    }
    openapiRepository.save(newValue);
  }

  /// 发布指定的对外接口
  ///
  /// 如果指定接口已发布，则不做处理
  ///
  /// @param id 要发布的对外接口的ID
  void publishOpenapi(String id) {
    Openapi entity = openapiRepository.findById(id).orElseThrow();
    entity.setStatus(OpenapiStatus.PUBLISHED);
    openapiRepository.save(entity);
  }

  /// 删除对外接口
  /// @param id 接口 ID
  /// @throws PublishedOpenapiCannotBeDeleteException 如果接口已发布，则抛出异常
  void deleteOpenapi(final String id) {
    openapiRepository.findById(id)
        .ifPresent(openapi -> {
          if (openapi.getStatus() == OpenapiStatus.PUBLISHED) {
            throw new PublishedOpenapiCannotBeDeleteException();
          }
          openapiRepository.deleteById(id);
        });
  }

  /// 检查接口编码是否重复
  ///
  /// @param openapi 要检查的对外接口对象
  /// @throws RepeatDataException 如果接口编码已存在且不是当前接口本身，则抛出异常
  private void checkRepeat(Openapi openapi) {
    Optional<Openapi> exist = openapiRepository.findByCode(openapi.getCode());
    if (exist.isPresent() && !Objects.equals(exist.get().getId(), openapi.getId())) {
      throw new RepeatDataException("`" + openapi.getCode() + "`");
    }
  }

  @Override
  public Page<@NonNull Openapi> listPublishedOpenapi(ODataQueryOption odata) {
    return openapiDAO.listPublishedOpenapi(odata);
  }

  @Override
  public @Nullable Openapi getByCode(String openapiCode) {
    return openapiRepository.findByCode(openapiCode).orElse(null);
  }
}
