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

package io.github.xezzon.zeroweb.app.internal;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.app.App;
import io.github.xezzon.zeroweb.app.entity.AddAppReq;
import io.github.xezzon.zeroweb.app.entity.UpdateAppReq;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 应用管理
///
/// @author xezzon
@RestController
@RequestMapping("/app")
public class AppHttpEndpoint {

  private final AppService appService;

  /// 构造函数，注入 [AppService]。
  ///
  /// @param appService [AppService] 实例。
  public AppHttpEndpoint(final AppService appService) {
    this.appService = appService;
  }

  /// 新增一个服务。
  ///
  /// 需要 `app:write` 权限。
  ///
  /// @param req 包含服务基础信息的新增服务请求体。
  /// @return 新增服务的 ID。
  @SaCheckPermission({PermissionConstant.APP_WRITE})
  @PostMapping()
  public Id addApp(@RequestBody @Valid final AddAppReq req) {
    final App app = req.into();
    appService.addApp(app);
    return Id.of(app.getId());
  }

  /// 查询所有服务列表。
  ///
  /// @return 包含所有服务实体的列表。
  @GetMapping()
  public List<App> listApp() {
    return appService.listApp();
  }

  /// 更新一个现有服务的信息。
  ///
  /// 需要 `app:write` 权限。
  ///
  /// @param req 包含要更新的服务信息和其 ID 的请求体。
  @SaCheckPermission({PermissionConstant.APP_WRITE})
  @PutMapping
  public void updateApp(@RequestBody @Valid final UpdateAppReq req) {
    final App app = req.into();
    appService.updateApp(app);
  }

  /// 根据服务ID删除一个服务。
  ///
  /// 需要 `app:write` 权限。
  ///
  /// @param id 要删除服务的 ID。
  @SaCheckPermission({PermissionConstant.APP_WRITE})
  @DeleteMapping("/{id}")
  public void deleteApp(@PathVariable @NotBlank final String id) {
    appService.deleteApp(id);
  }
}
