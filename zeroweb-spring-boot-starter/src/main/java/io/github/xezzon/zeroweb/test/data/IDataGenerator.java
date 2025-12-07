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

package io.github.xezzon.zeroweb.test.data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.TestOnly;

/**
 * 生成测试数据的规范。
 * @param <T> 测试数据类型
 * @author xezzon
 */
@SuppressWarnings("unused")
@TestOnly
public interface IDataGenerator<T> {

  /// 生成一个数据的方法
  /// @param i 测试数据的序号。有些测试数据的字段需要覆盖枚举值时可以用该字段。
  /// @return 测试数据
  T generateData(int i);

  /// 生成数据
  /// @param count 需要生成的测试数据的数量
  /// @return 测试数据列表
  default List<T> generate(int count) {
    return IntStream.range(0, count)
        .mapToObj(this::generateData)
        .collect(Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
              Collections.shuffle(list);
              return list;
            }
        ));
  }
}
