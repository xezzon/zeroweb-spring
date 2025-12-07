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

package io.github.xezzon.zeroweb.openapi.internal;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.openapi.Openapi;
import io.github.xezzon.zeroweb.openapi.entity.AddOpenapiReq;
import io.github.xezzon.zeroweb.openapi.entity.ModifyOpenapiReq;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 对外接口管理
///
/// @author xezzon
@RestController
@RequestMapping("/openapi")
public class OpenapiHttpEndpoint {

  private final OpenapiService openapiService;

  /// 依赖注入
  /// @param openapiService 对外接口管理
  public OpenapiHttpEndpoint(final OpenapiService openapiService) {
    this.openapiService = openapiService;
  }

  /// 新增`对外接口`
  ///
  /// @param req 包含添加`对外接口`请求数据的请求体
  /// @return 添加的`对外接口`的唯一标识符
  @PostMapping()
  @SaCheckPermission({PermissionConstant.OPENAPI_WRITE})
  public Id addOpenapi(@RequestBody @Valid AddOpenapiReq req) {
    Openapi openapi = req.into();
    openapiService.addOpenapi(openapi);
    return Id.of(openapi.getId());
  }

  /// 获取`对外接口`列表的分页数据
  ///
  /// @param odata OData查询参数，用于分页和排序
  /// @return 包含`对外接口`列表的分页对象
  @GetMapping()
  public Page<@NonNull Openapi> getOpenapiList(ODataRequestParam odata) {
    return openapiService.pageList(odata.into());
  }

  /// 更新`对外接口`信息
  ///
  /// @param req 包含更新`对外接口`请求数据的请求体
  @PutMapping()
  @SaCheckPermission({PermissionConstant.OPENAPI_WRITE})
  public void modifyOpenapi(@RequestBody ModifyOpenapiReq req) {
    Openapi openapi = req.into();
    openapiService.modifyOpenapi(openapi);
  }

  /// 发布指定的`对外接口`
  ///
  /// @param id 要发布的`对外接口`的唯一标识符
  @PutMapping("/publish/{id}")
  @SaCheckPermission({PermissionConstant.OPENAPI_PUBLISH})
  public void publishOpenapi(@PathVariable String id) {
    openapiService.publishOpenapi(id);
  }
}
