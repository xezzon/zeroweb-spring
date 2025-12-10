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

package io.github.xezzon.zeroweb.attachment.internal;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentReq;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.storage.DownloadEndpoint;
import io.github.xezzon.zeroweb.storage.StorageContext;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/// 附件管理
/// @author xezzon
@RestController
@RequestMapping("/attachment")
public class AttachmentHttpEndpoint {

  private final AttachmentService attachmentService;

  /// 依赖注入
  /// @param attachmentService 附件服务
  public AttachmentHttpEndpoint(
      final AttachmentService attachmentService
  ) {
    this.attachmentService = attachmentService;
  }

  /// 新增附件
  /// @param req 文件信息
  /// @param crc 文件的 CRC 值。虽然只有 S3 服务需要，但是建议调用接口时必传。
  /// @return 文件上传元数据
  @PostMapping()
  public UploadInfo addAttachment(
      @RequestBody @Valid final AddAttachmentReq req,
      @RequestParam(required = false) final String crc
  ) {
    Attachment attachment = req.into();
    return ScopedValue.where(StorageContext.CRC, crc)
        .call(() -> {
          attachmentService.addAttachment(attachment);
          return attachmentService.getUploadInfo(
              attachment.getId(),
              attachment.getChecksum(),
              attachment.getSize()
          );
        });
  }

  /// 获取附件上传元信息
  /// @param id 附件ID
  /// @param checksum 附件的内容摘要。与 `fileSize` 联合校验续传内容与之前的内容一致。
  /// @param fileSize 附件的大小。
  /// @param crc 文件的 CRC 值。虽然只有 S3 服务需要，但是建议调用接口时必传。
  /// @return 上传元信息
  @GetMapping("/{id}/resume")
  public UploadInfo getUploadInfo(
      @PathVariable @NotBlank final String id,
      @RequestParam @NotBlank final String checksum,
      @RequestParam @Positive final int fileSize,
      @RequestParam(required = false) final String crc
  ) {
    return ScopedValue.where(StorageContext.CRC, crc)
        .call(() -> attachmentService.getUploadInfo(id, checksum, fileSize));
  }

  /// 获取附件上传地址
  /// @param id 附件ID
  /// @param partNumber 分段序号
  /// @param crc 文件的 CRC 值。虽然只有 S3 服务需要，但是建议调用接口时必传。
  /// @return 附件上传地址
  @GetMapping("/{id}/endpoint/upload")
  public UploadEndpoint getUploadEndpoint(
      @PathVariable @NotBlank final String id,
      @RequestParam(required = false, defaultValue = "0") @Max(10_000) final int partNumber,
      @RequestParam(required = false) final String crc
  ) {
    return ScopedValue.where(StorageContext.CRC, crc)
        .call(() -> attachmentService.getUploadEndpoint(id, partNumber));
  }

  /// 文件上传完成后，将其状态变更为已完成
  /// @param id 附件ID
  @PutMapping("/{id}/status/done")
  public void finishUpload(@PathVariable @NotBlank final String id) {
    attachmentService.updateStatus(id);
  }

  /// 查询表单关联的附件
  /// @param bizType 业务类型
  /// @param bizId 业务ID
  /// @return 附件信息集合（不包含下载地址）
  @GetMapping("/list")
  public List<Attachment> queryByBiz(
      @RequestParam @NotBlank final String bizType,
      @RequestParam @NotBlank final String bizId
  ) {
    return attachmentService.queryByBiz(bizType, bizId);
  }

  /// 获取附件下载地址
  /// @param id 附件ID
  /// @return 附件下载地址
  @GetMapping("/{id}/endpoint/download")
  public DownloadEndpoint getDownloadEndpoint(@PathVariable @NotBlank final String id) {
    return attachmentService.getDownloadEndpoint(id);
  }

  /// 删除附件
  /// @param id 附件ID
  @DeleteMapping("/{id}")
  public void deleteAttachment(@PathVariable @NotBlank final String id) {
    attachmentService.deleteAttachment(id);
  }
}
