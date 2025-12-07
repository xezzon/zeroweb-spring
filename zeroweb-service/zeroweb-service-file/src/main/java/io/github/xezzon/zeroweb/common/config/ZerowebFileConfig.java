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

package io.github.xezzon.zeroweb.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/// 文件服务相关的配置属性类。
///
/// 该类通过 `@ConfigurationProperties` 注解与 `zeroweb.file` 前缀绑定，
/// 用于从 Spring Boot 配置文件中读取文件存储相关的自定义配置。
///
/// **用法示例:**
/// 在 `application.yml` 中配置：
/// ```yaml
/// zeroweb:
///   file:
///     provider: S3
/// ```
///
/// @author xezzon
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = ZerowebFileConfig.PREFIX)
public class ZerowebFileConfig {

  /// 配置属性的前缀
  public static final String PREFIX = ZerowebConfig.ZEROWEB + ".file";

  /// 文件存储服务提供者。
  /// 定义了 ZeroWeb 文件服务使用的后端存储类型。
  ///
  /// 默认为 [FileProviderEnum#FS] (文件系统存储)。
  private FileProviderEnum provider = FileProviderEnum.FS;
}
