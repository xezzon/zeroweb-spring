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

/**
 * 本系统相关配置
 * @author xezzon
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = ZerowebConfig.ZEROWEB)
public class ZerowebConfig {

  /**
   * 本系统相关配置的前缀
   */
  public static final String ZEROWEB = "zeroweb";
  /**
   * ID生成策略配置Key
   */
  public static final String ID_GENERATOR = "id-generator";

  /**
   * ID生成策略
   */
  protected IdGeneratorEnum idGenerator = IdGeneratorEnum.UUID;
  /**
   * JWT 相关配置
   */
  protected ZerowebJwtConfig jwt = new ZerowebJwtConfig();

  /**
   * JWT 相关配置
   */
  @Getter
  @Setter
  public static class ZerowebJwtConfig {

    /**
     * JWT 签发机构
     */
    protected String issuer = "xezzon.github.io";
    /**
     * JWT 有效时长，单位 秒
     */
    protected Long timeout = 120L;
  }

  /**
   * ID生成策略枚举值
   */
  public enum IdGeneratorEnum {
    /**
     * UUID
     */
    UUID,
  }
}
