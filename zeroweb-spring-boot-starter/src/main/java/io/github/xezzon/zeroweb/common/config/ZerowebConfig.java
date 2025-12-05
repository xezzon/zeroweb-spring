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

/// ZeroWeb 核心配置类，用于管理系统级别的各项配置。
/// 该类通过 `@ConfigurationProperties` 注解与 `zeroweb` 前缀绑定，支持从
/// `application.yml` 或 `application.properties` 等配置文件中读取自定义配置。
///
/// **用法示例:**
/// 在 `application.yml` 中配置：
/// ```yaml
/// zeroweb:
///   id-generator: UUID
///   jwt:
///     issuer: your-issuer.com
///     timeout: 3600
/// ```
///
/// @author xezzon
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = ZerowebConfig.ZEROWEB)
public class ZerowebConfig {

  /// ZeroWeb 配置项的前缀，所有相关配置都将以此前缀开始。
  public static final String ZEROWEB = "zeroweb";
  /// ID 生成策略的配置键。
  public static final String ID_GENERATOR = "id-generator";

  /// ID 生成策略，默认为 [IdGeneratorEnum#UUID]。
  protected IdGeneratorEnum idGenerator = IdGeneratorEnum.UUID;
  /// JWT（JSON Web Token）相关的配置，包括签发机构和有效时长。
  protected ZerowebJwtConfig jwt = new ZerowebJwtConfig();

  /// JWT (JSON Web Token) 的详细配置类。
  /// 包含 JWT 的签发者（issuer）和有效时间（timeout）等属性。
  @Getter
  @Setter
  public static class ZerowebJwtConfig {

    /// JWT 的签发机构（Issuer）。
    /// 默认为 `xezzon.github.io`。
    protected String issuer = "xezzon.github.io";
    /// JWT 的有效时长，单位为秒。
    /// 默认为 120 秒。
    protected Long timeout = 120L;
  }

  /// ID 生成策略的枚举值，定义了系统支持的各种 ID 生成方式。
  public enum IdGeneratorEnum {
    /// 使用 Universally Unique Identifier (UUID) 作为 ID 生成策略。
    UUID,
  }
}
