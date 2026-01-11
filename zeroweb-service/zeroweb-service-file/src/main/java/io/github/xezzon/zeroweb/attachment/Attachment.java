/*
 * SPDX-FileCopyrightText: Copyright (C) 2025-2026 xezzon
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

package io.github.xezzon.zeroweb.attachment;

import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/// 附件实体类，表示系统中存储的文件附件。
///
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_attachment")
@EntityListeners({AuditingEntityListener.class})
public class Attachment {

  /**
   * 附件的唯一标识符。
   */
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /**
   * 原始文件名，包含扩展名。
   */
  @Column(name = "file_name", nullable = false)
  String name;
  /**
   * 文件的内容摘要，用于验证文件完整性和唯一性。
   * 通常是文件的哈希值。
   */
  @Column(name = "checksum", nullable = false)
  String checksum;
  /// 文件的大小。
  /// 单位：`字节`。
  @Column(name = "file_size", nullable = false)
  Long size;
  /// 文件的 MIME 类型，例如 `image/jpeg`、`application/pdf`。
  @Column(name = "file_type", nullable = false)
  String type;
  /// 附件所属的业务类型。
  @Column(name = "biz_type", nullable = false)
  String bizType;
  /// 附件所属业务实体的ID。
  /// 与 [#bizType] 结合使用以关联附件。
  @Column(name = "biz_id", length = DatabaseConstant.ID_LENGTH)
  String bizId;
  /// 文件存储的后端提供者。
  @Column(name = "provider", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  FileProviderEnum provider;
  /// 附件的当前状态。
  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  AttachmentStatusEnum status;
  /// 上传此附件的用户ID。
  @Column(name = "owner_id", length = DatabaseConstant.ID_LENGTH)
  String ownerId;
  /// 附件的上传时间。
  /// 使用 [Instant] 存储，并由 [CreatedDate] 自动设置。
  @Column(name = "create_time", nullable = false, updatable = false)
  @CreatedDate
  Instant createTime;

  /// 生成附件在存储系统中的对象键（Object Key）。
  /// 对象键的格式为 `yyyy/MM/dd/id`，其中日期部分基于附件的创建时间（UTC）。
  ///
  /// @return 附件在存储系统中的唯一对象键。
  public String objectKey() {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneOffset.UTC)
        .format(this.createTime)
        + "/" + this.id;
  }
}
