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

package io.github.xezzon.zeroweb.attachment.internal;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.attachment.event.AttachmentCreatedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentDeletedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentUploadedEvent;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.common.exception.IncorrectFileException;
import io.github.xezzon.zeroweb.core.odata.ODataQueryOption;
import io.github.xezzon.zeroweb.storage.DownloadEndpoint;
import io.github.xezzon.zeroweb.storage.IStorageService;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/// 附件管理服务
/// @author xezzon
@Service
public class AttachmentService implements IAttachmentService {

  private final AttachmentRepository attachmentRepository;
  private final AttachmentDAO attachmentDAO;
  private final ZerowebFileConfig zerowebFileConfig;
  private final IStorageService.Factory storageServiceFactory;
  @Resource
  private ApplicationEventPublisher eventPublisher;

  /// 依赖注入
  /// @param attachmentRepository 附件 JPA 接口
  /// @param attachmentDAO 附件相关的数据库操作
  /// @param zerowebFileConfig 文件管理相关设置
  /// @param storageServiceFactory 用于获取存储操作服务实现类的工厂
  public AttachmentService(
      final AttachmentRepository attachmentRepository,
      final AttachmentDAO attachmentDAO,
      final ZerowebFileConfig zerowebFileConfig,
      IStorageService.Factory storageServiceFactory
  ) {
    this.attachmentRepository = attachmentRepository;
    this.attachmentDAO = attachmentDAO;
    this.zerowebFileConfig = zerowebFileConfig;
    this.storageServiceFactory = storageServiceFactory;
  }

  /// 根据附件ID查询附件信息
  /// @param id 附件唯一标识
  /// @return 附件实体对象
  /// @throws NoSuchElementException 当附件不存在时抛出
  @Override
  public Attachment queryById(String id) {
    return attachmentRepository.findById(id).orElseThrow();
  }

  /// 添加新附件记录
  /// 自动设置附件的存储提供商和所有者信息，并发布附件创建事件
  /// @param attachment 待添加的附件实体对象
  void addAttachment(Attachment attachment) {
    attachment.setProvider(zerowebFileConfig.getProvider());
    attachment.setOwnerId(JwtAuth.get()
        .map(JwtClaim::getSub)
        .orElse(null)
    );
    attachmentRepository.save(attachment);
    eventPublisher.publishEvent(new AttachmentCreatedEvent(attachment));
  }

  /// 获取文件上传信息
  /// 用于断点续传功能，验证文件校验和和大小是否匹配
  /// @param id 附件ID
  /// @param checksum 文件校验和
  /// @param fileSize 文件大小
  /// @return 上传信息对象，包含分片上传相关参数
  /// @throws IncorrectFileException 当校验和或文件大小不匹配时抛出
  UploadInfo getUploadInfo(String id, String checksum, long fileSize) {
    Attachment attachment = attachmentRepository.findById(id).orElseThrow();
    // 断点续传的内容要与之前的内容一致
    if (!Objects.equals(attachment.getChecksum(), checksum)) {
      throw new IncorrectFileException("Invalid checksum.");
    }
    if (!Objects.equals(attachment.getSize(), fileSize)) {
      throw new IncorrectFileException("Invalid size.");
    }
    IStorageService storageService = storageServiceFactory.get(attachment.getProvider());
    return storageService.getUploadInfo(attachment);
  }

  /// 获取文件上传访问点地址
  /// 支持普通上传和分片上传两种模式
  /// @param id 附件ID
  /// @param partNumber 分片编号，0表示普通上传，大于0表示分片上传
  /// @return 上传访问点信息，包含上传URL和相关参数
  UploadEndpoint getUploadEndpoint(String id, int partNumber) {
    Attachment attachment = this.queryById(id);
    IStorageService storageService = storageServiceFactory.get(attachment.getProvider());
    if (partNumber == 0) {
      return storageService.getUploadAddress(attachment);
    } else {
      return storageService.getUploadAddress(attachment, partNumber);
    }
  }

  /// 上传文件内容到存储服务
  /// @param attachment 附件信息
  /// @param content 文件字节内容
  void upload(Attachment attachment, byte[] content) {
    IStorageService storageService = storageServiceFactory.get(attachment.getProvider());
    storageService.upload(attachment, content);
  }

  /// 更新附件状态为已完成
  /// 发布附件上传完成事件，并更新附件状态
  /// @param id 附件ID
  void updateStatus(String id) {
    attachmentRepository.findById(id)
        .ifPresent(attachment -> {
          eventPublisher.publishEvent(new AttachmentUploadedEvent(attachment));
          attachment.setStatus(AttachmentStatusEnum.DONE);
          attachmentRepository.save(attachment);
        });
  }

  /// 根据业务信息查询附件列表
  /// @param bizType 业务类型
  /// @param bizId 业务ID
  /// @return 附件列表
  List<Attachment> queryByBiz(String bizType, String bizId) {
    return attachmentRepository.findByBizTypeAndBizId(bizType, bizId);
  }

  /// 获取文件下载访问点
  /// @param id 附件ID
  /// @return 下载访问点信息，包含下载URL、文件名等参数
  DownloadEndpoint getDownloadEndpoint(String id) {
    Attachment attachment = attachmentRepository.findById(id).orElseThrow();
    IStorageService storageService = storageServiceFactory.get(attachment.getProvider());
    DownloadEndpoint endpoint = storageService.getDownloadEndpoint(attachment);
    endpoint.setFilename(attachment.getName());
    return endpoint;
  }

  /// 下载文件内容
  /// @param attachment 附件信息
  /// @return 文件字节内容
  byte[] download(final Attachment attachment) {
    IStorageService storageService = storageServiceFactory.get(attachment.getProvider());
    return storageService.download(attachment);
  }

  /// 删除附件记录
  /// 从数据库中删除附件记录，并发布附件删除事件
  /// @param id 附件ID
  void deleteAttachment(String id) {
    attachmentRepository.findById(id)
        .ifPresent(attachment -> {
          attachmentRepository.deleteById(id);
          eventPublisher.publishEvent(new AttachmentDeletedEvent(attachment));
        });
  }

  /// 分页查询附件列表
  /// @param odata 查询参数
  /// @return 附件列表（分页）
  Page<Attachment> queryPage(final ODataQueryOption odata) {
    return attachmentDAO.findAll(odata);
  }
}
