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

/// 基于 UUID 的 ID 生成器实现。
///
/// 当 `zeroweb.id-generator` 配置属性设置为 "UUID" 或未设置时，此生成器将激活。
/// 它使用 [com.fasterxml.uuid.Generators#timeBasedEpochRandomGenerator()]
/// 生成基于时间戳的 UUID（UUIDv7），确保生成的 ID 具有唯一性和可排序性。
///
/// @author xezzon
@Component
@ConditionalOnProperty(
    prefix = ZerowebConfig.ZEROWEB,
    name = ZerowebConfig.ID_GENERATOR,
    havingValue = "UUID",
    matchIfMissing = true
)
public class UuidIdGenerator implements IdGenerator {

  private static final NoArgGenerator UUID_GENERATOR = Generators.timeBasedEpochRandomGenerator();

  /// 生成下一个 UUID 字符串作为 ID。
  ///
  /// @return 新生成的 UUID 字符串
  @Override
  public String nextId() {
    return UUID_GENERATOR.generate().toString();
  }
}
