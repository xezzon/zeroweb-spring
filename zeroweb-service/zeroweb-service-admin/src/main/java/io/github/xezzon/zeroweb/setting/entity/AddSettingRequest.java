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

package io.github.xezzon.zeroweb.setting.entity;

import static io.github.xezzon.zeroweb.common.constant.DatabaseConstant.NORMAL_STRING_LENGTH;

import io.github.xezzon.zeroweb.common.domain.CreateRequest;
import io.github.xezzon.zeroweb.common.validator.Alphanumeric;
import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import io.github.xezzon.zeroweb.setting.Setting;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// 新增业务参数请求
///
/// 用于创建新的业务参数配置，包含参数标识、约束定义和初始值。
/// 实现 [Into] 接口，提供转换为 [Setting] 实体对象的方法。
/// @param code 业务参数标识，唯一标识参数类型
/// @param schema 参数约束定义，JSON Schema 格式
/// @param value 参数初始值，JSON格式
/// @author xezzon
public record AddSettingRequest(
    @Alphanumeric @NotBlank @Size(max = NORMAL_STRING_LENGTH)
    String code,
    @NotNull
    String schema,
    Map<String, Object> value
) implements CreateRequest<Setting> {

  @Override
  public Setting into() {
    return Converter.INSTANCE.from(this);
  }

  @Mapper
  interface Converter extends From<AddSettingRequest, Setting> {

    Converter INSTANCE = Mappers.getMapper(Converter.class);

    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    Setting from(AddSettingRequest request);
  }
}
