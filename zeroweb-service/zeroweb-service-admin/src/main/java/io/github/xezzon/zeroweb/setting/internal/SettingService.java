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

package io.github.xezzon.zeroweb.setting.internal;

import io.github.xezzon.zeroweb.common.exception.RepeatDataException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.setting.ISettingService;
import io.github.xezzon.zeroweb.setting.Setting;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/// 业务参数服务
///
/// 提供业务参数的完整业务逻辑处理，包括新增、查询、更新、删除等操作。
/// 负责业务参数的数据验证、重复检查和事务管理。
/// @author xezzon
@Service
public class SettingService implements ISettingService {

  private final SettingDAO settingDAO;

  /// 依赖注入
  /// @param settingDAO 业务参数数据库操作
  public SettingService(final SettingDAO settingDAO) {
    this.settingDAO = settingDAO;
  }

  /// 查询指定的设置
  /// @param id 设置 ID
  Setting queryById(final String id) {
    return settingDAO.get().findById(id)
        .orElseThrow();
  }

  /// 新增配置项
  ///
  /// 向系统中添加新的业务参数配置，包含参数标识、约束定义和初始值。
  /// 在保存前会进行重复性检查，确保参数标识的唯一性。
  /// @param setting 要新增的配置项，包含完整的参数信息
  /// @throws RepeatDataException 若参数标识已存在则抛出
  void addSetting(final Setting setting) {
    this.checkRepeat(setting);
    settingDAO.get().save(setting);
  }

  /// 使用 OData 参数进行分页查询配置项
  ///
  /// 支持 OData 协议的复杂查询功能，包括过滤、排序、分页等操作。
  /// 默认按更新时间降序返回结果。
  /// @param odata OData 查询选项，包含查询条件、排序、分页等参数
  /// @return 分页结果，包含配置项列表和分页信息
  Page<@NonNull Setting> querySettingPage(final ODataQueryOption odata) {
    return settingDAO.findAll(odata);
  }

  /// 更新业务参数
  ///
  /// 更新现有业务参数的约束定义或参数值。
  /// 支持部分更新，仅修改指定的字段。
  /// @param setting 要更新的配置项，包含需要更新的字段
  void updateSetting(final Setting setting) {
    settingDAO.get().save(setting);
  }

  /// 删除业务参数
  ///
  /// 根据ID删除指定的业务参数配置。
  /// @param id 要删除的参数 ID
  void deleteSetting(final String id) {
    settingDAO.get().deleteById(id);
  }

  @Override
  public Setting queryByCode(@NonNull final String code) {
    return settingDAO.get().findByCode(code)
        .orElseThrow(() -> new NoSuchElementException("Setting `" + code + "` does not exist."));
  }

  /// 检查参数标识重复性
  ///
  /// 在新增或更新参数时检查标识是否已存在，防止重复配置。
  /// 允许更新自己的记录，不视为重复。
  /// @param setting 要检查的配置项，包含参数标识和ID信息
  /// @throws RepeatDataException 当发现重复的参数标识时抛出
  private void checkRepeat(final Setting setting) {
    Optional<Setting> exist = settingDAO.get().findByCode(setting.getCode());
    if (exist.isPresent() && !Objects.equals(exist.get().getId(), setting.getId())) {
      throw new RepeatDataException(setting.getCode());
    }
  }
}
