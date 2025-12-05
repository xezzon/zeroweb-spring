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

package io.github.xezzon.zeroweb.common.context;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/// 提供对 Spring 应用上下文的静态访问。
/// 该类通过实现 [ApplicationContextAware] 接口，在 Spring 容器初始化时自动设置应用上下文。
/// 允许应用程序中非 Spring 管理的组件获取 Spring Bean。
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  /// 存储 Spring 应用上下文的静态实例。
  /// 它是私有的，只能通过该类的静态方法进行访问和设置。
  private static ApplicationContext applicationContext;

  /// 获取 Spring 应用上下文。
  ///
  /// 该方法允许任何组件在应用程序中获取已初始化的 Spring 应用上下文，
  /// 从而访问由 Spring 管理的 Bean。
  ///
  /// @return 当前的 Spring 应用上下文。
  public static ApplicationContext get() {
    return applicationContext;
  }

  /// 设置 Spring 应用上下文。
  ///
  /// 该私有方法用于将传入的 [ApplicationContext] 实例赋给静态变量 [ApplicationContextProvider#applicationContext]。
  /// 通常由 [ApplicationContextProvider#setApplicationContext(ApplicationContext)] 方法调用。
  ///
  /// @param context 要设置的 Spring 应用上下文。
  private static void setContext(ApplicationContext context) {
    applicationContext = context;
  }

  /// 实现 [ApplicationContextAware] 接口，由 Spring 容器自动调用。
  ///
  /// 当应用上下文可用时，Spring 会自动将其实例注入到此方法中。
  /// 该方法将传入的上下文设置到静态变量中，供其他部分使用。
  ///
  /// @param applicationContext 由 Spring 注入的应用上下文实例。
  @Override
  public void setApplicationContext(@NonNull final ApplicationContext applicationContext) {
    setContext(applicationContext);
  }
}
