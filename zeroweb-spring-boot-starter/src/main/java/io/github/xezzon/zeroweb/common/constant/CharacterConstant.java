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

package io.github.xezzon.zeroweb.common.constant;

/// 字符常量类，提供常用字符集数组。
/// 此类为工具类，包含小写字母、大写字母和数字字符的静态常量数组。
/// 它旨在提供方便且预初始化的字符集，以避免在运行时重复创建。
///
/// @author xezzon
public class CharacterConstant {

  /// 小写字符集数组，包含从 'a' 到 'z' 的所有小写英文字母。
  private static final char[] LOWERCASE = new char['z' - 'a' + 1];
  static {
    for (int i = 0, cnt = LOWERCASE.length; i < cnt; i++) {
      LOWERCASE[i] = (char) ('a' + i);
    }
  }

  /// 获取小写字符集数组。
  ///
  /// @return 包含所有小写英文字母的字符数组。
  public static char[] getLowercase() {
    return LOWERCASE;
  }

  /// 大写字符集数组，包含从 'A' 到 'Z' 的所有大写英文字母。
  private static final char[] UPPERCASE = new char['Z' - 'A' + 1];
  static {
    for (int i = 0, cnt = UPPERCASE.length; i < cnt; i++) {
      UPPERCASE[i] = (char) ('A' + i);
    }
  }

  /// 获取大写字符集数组。
  ///
  /// @return 包含所有大写英文字母的字符数组。
  public static char[] getUppercase() {
    return UPPERCASE;
  }

  /// 数字字符集数组，包含从 '0' 到 '9' 的所有数字字符。
  private static final char[] DIGIT = new char[10];
  static {
    for (int i = 0, cnt = DIGIT.length; i < cnt; i++) {
      DIGIT[i] = (char) ('0' + i);
    }
  }

  /// 获取数字字符集数组。
  ///
  /// @return 包含所有数字字符的字符数组。
  public static char[] getDigit() {
    return DIGIT;
  }

  /// 私有构造函数，防止实例化。
  private CharacterConstant() {
  }
}
