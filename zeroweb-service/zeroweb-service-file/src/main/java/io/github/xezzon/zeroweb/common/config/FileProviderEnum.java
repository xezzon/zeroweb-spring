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

package io.github.xezzon.zeroweb.common.config;

/// 文件存储后端枚举。
///
/// 定义了支持的文件存储服务类型，例如文件系统 (FS) 和 S3 兼容的对象存储。
/// @author xezzon
public enum FileProviderEnum {
    /// 硬盘存储：文件将直接存储在本地文件系统上。
    FS,
    /// 对象存储：文件将存储在兼容 S3 协议的对象存储服务上。
    S3,
}
