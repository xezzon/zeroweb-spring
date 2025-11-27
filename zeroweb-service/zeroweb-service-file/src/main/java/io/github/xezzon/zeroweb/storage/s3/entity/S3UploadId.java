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

package io.github.xezzon.zeroweb.storage.s3.entity;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// S3 的上传 ID
/// 
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_s3_upload_id")
public class S3UploadId {

  /// 附件ID
  @Id
  @Column(
      name = "attachment_id",
      nullable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  private String attachmentId;
  /// S3上传ID
  @Column(name = "upload_id", nullable = false, length = 512)
  private String uploadId;
  /// 循环冗余校验和。用于 S3 的完整对象校验
  @Column(name = "crc", nullable = false, updatable = false)
  private String crc;

  public S3UploadId() {
    super();
  }

  public S3UploadId(String attachmentId, String uploadId, String crc) {
    this.attachmentId = attachmentId;
    this.uploadId = uploadId;
    this.crc = crc;
  }
}
