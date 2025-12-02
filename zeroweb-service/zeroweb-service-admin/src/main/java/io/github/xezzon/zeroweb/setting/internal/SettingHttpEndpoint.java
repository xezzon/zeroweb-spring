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

package io.github.xezzon.zeroweb.setting.internal;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.xezzon.zeroweb.common.domain.Id;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.entity.AddSettingRequest;
import io.github.xezzon.zeroweb.setting.entity.UpdateSchemaRequest;
import io.github.xezzon.zeroweb.setting.entity.UpdateValueRequest;
import java.time.Instant;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// 业务参数管理
/// @author xezzon
@RestController
@RequestMapping("/setting")
public class SettingHttpEndpoint {

  private final SettingService settingService;

  public SettingHttpEndpoint(final SettingService settingService) {
    this.settingService = settingService;
  }

  /// 新增业务参数
  /// @param request 业务参数
  /// @return ID
  @SaCheckPermission({PermissionConstant.SETTING_WRITE})
  @PostMapping()
  Id addSetting(@RequestBody final AddSettingRequest request) {
    Setting setting = request.into();
    setting.setUpdateTime(Instant.now());
    settingService.addSetting(setting);
    return Id.of(setting.getId());
  }

  /// 查询业务参数列表（分页）
  /// @param odata 查询参数
  /// @return 业务参数列表
  @SaCheckPermission({PermissionConstant.SETTING_READ})
  @GetMapping()
  Page<@NonNull Setting> querySettingPage(final ODataRequestParam odata) {
    return settingService.querySettingPage(odata.into());
  }

  @GetMapping("/{code}")
  Setting queryByCode(@PathVariable @NonNull final String code) {
    return settingService.queryByCode(code);
  }

  /**
   * 更新业务参数
   * @param request 业务参数
   */
  @SaCheckPermission({PermissionConstant.SETTING_WRITE})
  @PutMapping("/schema")
  void updateSettingSchema(@RequestBody final UpdateSchemaRequest request) {
    Setting setting = request.into();
    settingService.updateSetting(setting);
  }

  /**
   * 更新业务参数（仅更新值）
   * @param request 业务参数
   */
  @SaCheckPermission({PermissionConstant.SETTING_READ})
  @PutMapping("/value")
  void updateSettingValue(@RequestBody final UpdateValueRequest request) {
    Setting setting = request.into();
    settingService.updateSetting(setting);
  }

  /**
   * 删除业务参数
   * @param id 业务参数ID
   */
  @SaCheckPermission({PermissionConstant.SETTING_WRITE})
  @DeleteMapping("/{id}")
  void deleteSetting(@PathVariable final String id) {
    settingService.deleteSetting(id);
  }
}
