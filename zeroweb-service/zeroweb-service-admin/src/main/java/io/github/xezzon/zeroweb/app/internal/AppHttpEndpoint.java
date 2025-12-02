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
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 服务管理
///
/// @author xezzon
@RestController
@RequestMapping("/app")
public class AppHttpEndpoint {

  private final AppService appService;

  public AppHttpEndpoint(final AppService appService) {
    this.appService = appService;
  }

  /// 新增服务
  ///
  /// @param req 服务基础信息
  @SaCheckPermission({PermissionConstant.APP_WRITE})
  @PostMapping()
  public Id addApp(@RequestBody @Validated final AddAppReq req) {
    final App app = req.into();
    appService.addApp(app);
    return Id.of(app.getId());
  }

  /// 查询服务列表
  ///
  /// @return 服务列表
  @GetMapping()
  public List<App> listApp() {
    return appService.listApp();
  }

  /// 更新服务
  ///
  /// @param req 服务基础信息
  @SaCheckPermission({PermissionConstant.APP_WRITE})
  @PutMapping
  public void updateApp(@RequestBody @Validated final UpdateAppReq req) {
    final App app = req.into();
    appService.updateApp(app);
  }

  /// 删除服务
  ///
  /// @param id 服务ID
  @SaCheckPermission({PermissionConstant.APP_WRITE})
  @DeleteMapping("/{id}")
  public void deleteApp(@PathVariable final String id) {
    appService.deleteApp(id);
  }
}
