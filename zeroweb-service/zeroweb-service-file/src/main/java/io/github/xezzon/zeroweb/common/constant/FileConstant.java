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

/// file 模块相关的常量
/// @author xezzon
public class FileConstant {

  /// 最大分段数量。采用 S3 的约束。
  /// @see <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/qfacts.html">Amazon S3 multipart upload limits</a>
  public static final int MAX_MULTIPART_NUMBER = 10_000;

  /// 私有化构造函数，防止实例化。
  private FileConstant() {
  }
}
