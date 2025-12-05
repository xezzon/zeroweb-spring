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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/// 从测试数据生成 INSERT DML 语句的规范
/// @param <T> 测试数据类型
/// @author xezzon
@SuppressWarnings("unused")
public interface ToSQL<T> {

  /// 申明表名
  /// @return 表名
  String tableName();

  /// 申明列名
  /// @return 列名列表
  String[] columnNames();

  /// 获取列数据的方法
  /// @return 获取数据的函数的集合。与 [列名](#columnNames()) 一一对应。
  List<Function<T, Object>> columnValue();

  /// 列数据模板
  ///
  /// 如果全部是字符串则可以使用默认方法
  ///
  /// @return 默认值 `('%s', '%s',...),`
  default String columnTemplate() {
    return IntStream.range(0, columnNames().length)
        .mapToObj(_ -> "'%s'")
        .collect(Collectors.joining(",", "(", "),"));
  }

  /**
   * 将数据转换为 INSERT DML 语句
   * @param dataset 数据集合
   * @return INSERT DML 语句
   */
  default String toSql(List<T> dataset) {
    StringBuilder sqlBuilder = new StringBuilder();
    // 表头
    sqlBuilder.append("INSERT INTO ").append(tableName()).append("(");
    for (String columnName : columnNames()) {
      sqlBuilder.append(columnName).append(',');
    }
    sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
    sqlBuilder.append(") VALUES ");
    // 数据
    for (T data : dataset) {
      Object[] values = columnValue().stream()
          .map(fn -> fn.apply(data))
          .toArray();
      sqlBuilder.append(String.format(columnTemplate(), values));
    }

    sqlBuilder.setCharAt(sqlBuilder.length() - 1, ';');
    return sqlBuilder.toString();
  }
}
