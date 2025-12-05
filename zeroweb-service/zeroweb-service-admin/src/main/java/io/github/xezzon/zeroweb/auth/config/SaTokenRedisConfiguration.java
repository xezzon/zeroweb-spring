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

package io.github.xezzon.zeroweb.auth.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import io.github.xezzon.zeroweb.common.redis.RedisTemplateFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

/// `SaTokenRedisConfiguration` 是 Sa-Token 框架的 Redis 配置类。
///
/// 它在应用程序启动时检查是否配置了 Redis。
/// 如果没有配置 Redis，则使用内存作为 Session 的存储方式；否则使用 Redis。
///
/// @author xezzon
@Configuration
@ConditionalOnMissingBean(RedisTemplateFactory.class)
public class SaTokenRedisConfiguration implements ApplicationRunner {

  /// 应用程序启动时运行的方法。
  ///
  /// 如果没有配置 Redis，则将 Sa-Token 的 DAO 实现设置为默认的内存实现。
  ///
  /// @param args 应用程序启动参数。
  @Override
  public void run(@NonNull final ApplicationArguments args) {
    SaManager.setSaTokenDao(new SaTokenDaoDefaultImpl());
  }
}
