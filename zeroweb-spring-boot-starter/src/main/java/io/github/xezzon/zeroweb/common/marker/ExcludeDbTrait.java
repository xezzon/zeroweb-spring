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

/**
 * @author xezzon
 */
@TestOnly
public class ExcludeDbTrait implements ImportBeanDefinitionRegistrar {

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
