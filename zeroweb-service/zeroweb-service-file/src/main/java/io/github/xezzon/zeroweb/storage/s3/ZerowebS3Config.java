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

package io.github.xezzon.zeroweb.storage.s3;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import java.net.URI;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;

/// 将 S3 作为存储后端的配置。
/// 如果没有配置存储桶，则不会注册 Bean。
/// @author xezzon
@Setter
@Configuration
@ConfigurationProperties(prefix = ZerowebS3Config.PREFIX)
@ConditionalOnProperty(name = "S3_BUCKET")
public class ZerowebS3Config {

  static final String PREFIX = ZerowebFileConfig.PREFIX + ".s3";

  /// S3 调用地址。
  ///
  /// 如果是 AWS S3，无需填写；如果是其他兼容 S3 的服务，则必填。
  private URI endpoint;
  /// S3 的 Access Key
  private String accessKey;
  /// S3 的 Secret Key
  private String secretKey;
  /// S3 服务所在区域
  ///
  /// 兼容 S3 的服务无需填写
  private String region;
  /// 存储桶
  @Getter
  private String bucket;
  /// 单个分片最大大小
  ///
  /// 大于该大小的需要进行分片上传
  ///
  /// 单位 MB
  private Integer partSize = 5;

  /// 单个分片最大大小
  ///
  /// 大于该大小的需要进行分片上传
  ///
  /// @return 单个分片的最大大小。单位 Byte。
  public long getPartSize() {
    return this.partSize * 1024L * 1024;
  }

  /// 创建 Bean：S3 服务的凭据
  /// @return S3 服务的凭据提供器
  /// @see <a href="https://docs.amazonaws.cn/en_us/sdk-for-java/latest/developer-guide/credentials-chain.html">Default credentials provider chain in the Amazon SDK for Java 2.x</a>
  @Bean
  AwsCredentialsProvider credentialsProvider() {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }

  /// 创建 Bean：S3 预签名客户端
  /// @param credentialsProvider 注入：凭据提供器
  /// @return S3 预签名客户端
  @Bean
  S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
    Builder builder = S3Presigner.builder();
    if (endpoint != null) {
      builder.endpointOverride(endpoint);
    }
    return builder
        .credentialsProvider(credentialsProvider)
        .region(Region.of(region))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()
        )
        .build();
  }

  /// 创建 Bean：S3 Java 客户端
  /// @param credentialsProvider 注入：凭据提供器
  /// @return S3 Java 客户端
  @Bean
  S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
    S3ClientBuilder builder = S3Client.builder();
    if (endpoint != null) {
      builder.endpointOverride(endpoint);
    }
    return builder
        .credentialsProvider(credentialsProvider)
        .region(Region.of(region))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()
        )
        .build();
  }
}

/// 如果当前配置的存储方式是 [FileProviderEnum#S3]，要求 [ZerowebS3Config] 被注册。
@Component
@ConditionalOnProperty(prefix = ZerowebFileConfig.PREFIX, name = "provider", havingValue = "S3")
@SuppressWarnings("unused")
class S3ConfigValidator {

  /// 校验 S3 相关配置是否正确。
  /// @param s3Config S3 相关配置
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  S3ConfigValidator(Optional<ZerowebS3Config> s3Config) {
    s3Config.orElseThrow(() -> new UnsupportedFileProviderException(FileProviderEnum.S3));
  }
}
