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

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.core.trait.From;
import io.github.xezzon.zeroweb.core.trait.Into;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// @author xezzon
/// @param name 文件名
/// @param checksum 文件摘要
/// @param size 文件大小。单位：字节。
/// @param type MIME 类型
/// @param bizType 业务类型
/// @param bizId 业务ID
public record AddAttachmentReq(
    String name,
    String checksum,
    Long size,
    String type,
    String bizType,
    String bizId
) implements Into<Attachment> {

  @Override
  public Attachment into() {
    return Mappers.getMapper(Converter.class).from(this);
  }

  @Mapper
  interface Converter extends From<AddAttachmentReq, Attachment> {

    @Mapping(target = "status", constant = "UPLOADING")
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    Attachment from(AddAttachmentReq source);
  }
}
