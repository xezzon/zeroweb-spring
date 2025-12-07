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

package io.github.xezzon.zeroweb.common.marker;

import org.jetbrains.annotations.TestOnly;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/// `ExcludeDbTrait` 是一个用于在测试环境中排除所有 [DbTrait] 类型 Bean 定义的 [ImportBeanDefinitionRegistrar]。
///
/// 它主要用于单元测试或集成测试，以确保数据库相关的 Bean 不被加载，从而隔离测试环境，避免不必要的数据库操作。
///
/// 通过实现 [ImportBeanDefinitionRegistrar] 接口，它可以在 Spring 容器启动时动态地注册或修改 Bean 定义。
///
/// @author xezzon
/// @see TestOnly
/// @see DbTrait
/// @see ImportBeanDefinitionRegistrar
@TestOnly
public class ExcludeDbTrait implements ImportBeanDefinitionRegistrar {

  /// 注册 Bean 定义的回调方法。此方法会在 Spring 容器初始化期间被调用。
  ///
  /// 在此实现中，如果 `registry` 是一个 [ListableBeanFactory] 的实例，
  /// 它将查找所有类型为 [DbTrait] 的 Bean 定义，并从注册表中移除它们。
  /// 这有效地阻止了任何实现了 [DbTrait] 接口的 Bean 被 Spring 容器加载，
  /// 从而在测试环境中排除了数据库相关的组件。
  ///
  /// @param metadata 当前正在处理的 `@Configuration` 类的注解元数据。
  /// @param registry 用于注册 Bean 定义的注册表。
  @Override
  public void registerBeanDefinitions(
      @NonNull final AnnotationMetadata metadata,
      @NonNull final BeanDefinitionRegistry registry
  ) {
    if (registry instanceof ListableBeanFactory factory) {
      String[] beanNames = factory.getBeanNamesForType(DbTrait.class);
      for (String beanName : beanNames) {
        registry.removeBeanDefinition(beanName);
      }
    }
  }
}
