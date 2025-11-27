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

package io.github.xezzon.zeroweb.attachment.entity;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;

/// 文件上传信息
/// @param id 附件ID
/// @param provider 存储提供商
/// @param partCount 分片数量
/// @param partSize 分片大小。单位 Byte。
/// @author xezzon
public record UploadInfo(
    String id,
    FileProviderEnum provider,
    int partCount,
    long partSize
) {
}
