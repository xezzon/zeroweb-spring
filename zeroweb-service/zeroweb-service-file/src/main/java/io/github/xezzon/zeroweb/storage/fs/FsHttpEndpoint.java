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

package io.github.xezzon.zeroweb.storage.fs;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/// 上传/下载附件到文件系统
/// @author xezzon
@RestController
@ConditionalOnBean(ZerowebFsConfig.class)
public class FsHttpEndpoint {

  private final FsService fsService;
  private final IAttachmentService attachmentService;

  /**
   * 注入依赖
   * @param fsService 文件系统存储服务
   * @param attachmentService 附件服务
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public FsHttpEndpoint(
      final Optional<FsService> fsService,
      final IAttachmentService attachmentService
  ) {
    this.fsService = fsService.orElse(null);
    this.attachmentService = attachmentService;
  }

  /// 上传文件到服务器磁盘
  /// 
  /// @param id          附件ID
  /// @param fileContent 文件内容
  @PutMapping(FsService.UPLOAD_ENDPOINT)
  public void upload(
      @PathVariable @NotBlank final String id,
      @RequestBody @NotEmpty final byte[] fileContent
  ) {
    fsService().upload(id, fileContent);
  }

  /**
   * 上传文件分段到服务器磁盘
   * @param id 附件ID
   * @param partNumber 分段序号
   * @param fileContent 文件内容
   */
  @PutMapping(FsService.MULTIPART_UPLOAD_ENDPOINT)
  public void upload(
      @PathVariable @NotBlank final String id,
      @PathVariable @Max(10_000) final int partNumber,
      @RequestBody @NotEmpty final byte[] fileContent
  ) {
    fsService().upload(id, partNumber, fileContent);
  }

  /// 下载文件
  /// 
  /// @param id 附件ID
  /// @return 文件内容
  @GetMapping(FsService.DOWNLOAD_ENDPOINT)
  public ResponseEntity<byte @NonNull []> download(@PathVariable @NotBlank final String id) {
    Attachment attachment = attachmentService.queryById(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(attachment.getType()));
    byte[] fileContent = fsService().download(id);
    return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
  }

  /// 获取文件系统存储服务。
  /// 在未正确配置文件存储时，调用相关的 HTTP 接口会报错。
  private FsService fsService() {
    if (fsService == null) {
      throw new UnsupportedFileProviderException(FileProviderEnum.FS);
    }
    return fsService;
  }
}
