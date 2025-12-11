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
import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.common.metadata.PermissionConstant;
import io.github.xezzon.zeroweb.core.odata.ODataRequestParam;
import io.github.xezzon.zeroweb.setting.Setting;
import io.github.xezzon.zeroweb.setting.entity.AddSettingRequest;
import io.github.xezzon.zeroweb.setting.entity.UpdateSchemaRequest;
import io.github.xezzon.zeroweb.setting.entity.UpdateValueRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

  /// 依赖注入
  /// @param settingService 业务参数管理服务
  public SettingHttpEndpoint(final SettingService settingService) {
    this.settingService = settingService;
  }

  /// 新增业务参数
  ///
  /// 向系统中添加新的业务参数配置，包含参数标识、约束定义和初始值。
  /// 需要写入权限验证。
  /// @param request 新增请求，包含完整的参数信息
  /// @return 新创建参数的ID
  /// @throws RepeatDataException 当参数标识重复时抛出
  @SaCheckPermission({PermissionConstant.SETTING_WRITE})
  @PostMapping()
  Id addSetting(@RequestBody @Valid final AddSettingRequest request) {
    Setting setting = request.into();
    setting.setUpdateTime(Instant.now());
    settingService.addSetting(setting);
    return Id.of(setting.getId());
  }

  /// 查询业务参数列表（分页）
  ///
  /// 支持OData协议的复杂查询功能，包括过滤、排序、分页等操作。
  /// 需要查看权限验证。
  /// @param odata 查询参数，包含查询条件、排序、分页等
  /// @return 分页结果，包含参数列表和分页信息
  @SaCheckPermission({PermissionConstant.SETTING_READ})
  @GetMapping()
  Page<@NonNull Setting> querySettingPage(final ODataRequestParam odata) {
    return settingService.querySettingPage(odata.into());
  }

  /// 根据参数标识查询配置项
  ///
  /// 通过业务参数的唯一标识符查询具体的参数配置。
  /// @param code 业务参数标识
  /// @return 查找到的配置项
  @GetMapping("/{code}")
  Setting queryByCode(@PathVariable @NonNull final String code) {
    return settingService.queryByCode(code);
  }

  /// 更新业务参数
  ///
  /// 更新现有业务参数的约束定义和参数值。
  /// 会同时更新schema和value字段，并更新时间戳。
  /// 需要写入权限验证。
  /// @param request 更新请求，包含参数ID、约束定义和参数值
  @SaCheckPermission({PermissionConstant.SETTING_WRITE})
  @PutMapping("/schema")
  void updateSettingSchema(@RequestBody @Valid final UpdateSchemaRequest request) {
    Setting setting = request.into();
    settingService.updateSetting(setting);
  }

  /// 更新业务参数（仅更新值）
  ///
  /// 仅更新业务参数的值，不修改约束定义和参数标识。
  /// 需要查看权限验证。
  /// @param request 更新请求，包含参数ID和新值
  @SaCheckPermission({PermissionConstant.SETTING_READ})
  @PutMapping("/value")
  void updateSettingValue(@RequestBody @Valid final UpdateValueRequest request) {
    Setting setting = request.into();
    settingService.updateSetting(setting);
  }

  /// 删除业务参数
  ///
  /// 根据ID删除指定的业务参数配置。
  /// 删除操作不可逆，请谨慎使用。
  /// 需要写入权限验证。
  /// @param id 要删除的参数ID
  @SaCheckPermission({PermissionConstant.SETTING_WRITE})
  @DeleteMapping("/{id}")
  void deleteSetting(@PathVariable @NotBlank final String id) {
    settingService.deleteSetting(id);
  }
}
