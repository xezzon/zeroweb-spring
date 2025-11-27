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

/// 应用启动时检查是否配置了 Redis
///
/// 没有配置 Redis 时，使用内存作为 Session 的存储方式
///
/// 否则以 Redis 作为 Session 的存储方式
/// @author xezzon
@Configuration
@ConditionalOnMissingBean(RedisTemplateFactory.class)
public class SaTokenRedisConfiguration implements ApplicationRunner {

  @Override
  public void run(@NonNull final ApplicationArguments args) {
    SaManager.setSaTokenDao(new SaTokenDaoDefaultImpl());
  }
}
