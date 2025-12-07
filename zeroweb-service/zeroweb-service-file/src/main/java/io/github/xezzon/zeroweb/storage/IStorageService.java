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

package io.github.xezzon.zeroweb.storage;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/// 文件存储抽象接口
/// @author xezzon
public interface IStorageService {

  /// 申明附件的存储类型
  ///
  /// @return [FileProviderEnum] 存储类型枚举
  FileProviderEnum provider();

  /// 获取上传元数据
  ///
  /// 为给定的附件生成并返回上传所需的元数据信息。
  /// @param attachment [Attachment] 附件对象，包含文件相关信息
  /// @return [UploadInfo] 上传元数据对象
  UploadInfo getUploadInfo(Attachment attachment);

  /// 获取附件的上传地址
  ///
  /// 为给定的附件生成一个用于直接上传文件的地址。
  /// @param attachment [Attachment] 附件对象，包含文件相关信息
  /// @return [UploadEndpoint] 上传地址对象
  UploadEndpoint getUploadAddress(Attachment attachment);

  /// 获取附件分段上传地址
  ///
  /// 为给定附件的特定分段生成上传地址。
  /// 此方法适用于需要分段上传大文件的情况。
  /// @param attachment [Attachment] 附件对象，包含文件相关信息
  /// @param partNumber 文件分段的序号
  /// @return [UploadEndpoint] 分段上传地址对象
  UploadEndpoint getUploadAddress(Attachment attachment, int partNumber);

  /// 获取附件下载地址
  ///
  /// 为给定的附件生成一个用于下载文件的地址。
  /// @param attachment [Attachment] 附件信息对象
  /// @return [DownloadEndpoint] 下载地址对象
  DownloadEndpoint getDownloadEndpoint(Attachment attachment);

  /// 上传文件
  ///
  /// 将文件内容上传到存储服务。
  /// @param attachment [Attachment] 附件对象，包含文件相关信息
  /// @param fileContent 文件内容的字节数组
  void upload(Attachment attachment, byte[] fileContent);

  /// 下载文件
  ///
  /// 从存储服务下载指定附件的内容。
  /// @param attachment [Attachment] 附件对象，包含文件相关信息
  /// @return 文件内容的字节数组
  byte[] download(Attachment attachment);

  /// 文件存储服务工厂类
  ///
  /// 负责根据存储类型提供对应的 [IStorageService] 实例。
  @Component
  class Factory {

    private final Map<FileProviderEnum, IStorageService> serviceMap;

    /// 构造函数，注入所有 [IStorageService] 实现
    ///
    /// @param storageServices 所有可用的 [IStorageService] 实现列表
    Factory(final List<IStorageService> storageServices) {
      this.serviceMap = storageServices.stream()
          .collect(Collectors.toMap(IStorageService::provider, Function.identity()));
    }

    /// 根据文件提供者类型获取对应的存储服务
    ///
    /// @param provider 文件提供者类型
    /// @return 对应的 [IStorageService] 实例
    /// @throws UnsupportedFileProviderException 如果找不到对应的存储服务实现
    public IStorageService get(FileProviderEnum provider) {
      IStorageService storageService = serviceMap.get(provider);
      if (storageService == null) {
        throw new UnsupportedFileProviderException(provider);
      }
      return storageService;
    }
  }
}
