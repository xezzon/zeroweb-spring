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

package io.github.xezzon.zeroweb.common.jpa;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import io.github.xezzon.zeroweb.common.config.ZerowebConfig;
import io.github.xezzon.zeroweb.core.trait.IdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * ID生成器（UUID策略）
 * @author xezzon
 */
@Component
@ConditionalOnProperty(
    prefix = ZerowebConfig.ZEROWEB,
    name = ZerowebConfig.ID_GENERATOR,
    havingValue = "UUID",
    matchIfMissing = true
)
public class UuidIdGenerator implements IdGenerator {

  private static final NoArgGenerator UUID_GENERATOR = Generators.timeBasedEpochRandomGenerator();

  @Override
  public String nextId() {
    return UUID_GENERATOR.generate().toString();
  }
}
