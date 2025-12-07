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

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.servlet.autoconfigure.MultipartProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/// 文件系统存储配置。
/// 如果没有配置附件存储的基础路径，则不会注册 Bean。
///
/// @author xezzon
@Setter
@Configuration
@ConfigurationProperties(prefix = ZerowebFsConfig.PREFIX)
@ConditionalOnProperty(name = "ZEROWEB_FS_BASEPATH")
public class ZerowebFsConfig {

  static final String PREFIX = ZerowebFileConfig.PREFIX + ".fs";

  /// 文件存储基础路径
  private String basePath;
  /// Web 容器上传配置
  @Resource
  private MultipartProperties multipartProperties;

  /// 应用启动后，创建文件上传基础目录（如果不存在）
  /// @throws IOException 创建目录失败
  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(this.getBasePath());
  }

  /// 获取文件上传基础路径
  /// @return  文件上传基础路径
  public Path getBasePath() {
    return Path.of(basePath);
  }

  /// 获取文件存储的分片大小
  /// @return 文件存储的分片大小。单位 Byte
  public long getPartSize() {
    return multipartProperties.getMaxFileSize().toBytes();
  }
}

/// 如果当前配置的存储方式是 [FileProviderEnum#FS]，要求 [ZerowebFsConfig] 被注册。
@Component
@ConditionalOnProperty(prefix = ZerowebFileConfig.PREFIX, name = "provider", havingValue = "FS")
@SuppressWarnings("unused")
class FsConfigValidator {

  /// 校验文件系统相关配置是否正确
  /// @param fsConfig 文件系统配置
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  FsConfigValidator(Optional<ZerowebFsConfig> fsConfig) {
    fsConfig.orElseThrow(() -> new UnsupportedFileProviderException(FileProviderEnum.FS));
  }
}
