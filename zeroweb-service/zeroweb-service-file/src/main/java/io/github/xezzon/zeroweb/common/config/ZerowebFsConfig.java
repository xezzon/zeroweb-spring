package io.github.xezzon.zeroweb.common.config;

import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/// 文件系统存储配置
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

  @PostConstruct
  public void init() throws IOException {
    // 创建基础目录（如果不存在）
    Files.createDirectories(this.getBasePath());
  }

  public Path getBasePath() {
    return Path.of(basePath);
  }

  /// @return 文件存储的分片大小。单位 Byte
  public long getPartSize() {
    return multipartProperties.getMaxFileSize().toBytes();
  }
}

@Component
@ConditionalOnProperty(prefix = ZerowebFileConfig.PREFIX, name = "provider", havingValue = "FS")
@SuppressWarnings("unused")
class FsConfigValidator {

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  FsConfigValidator(Optional<ZerowebFsConfig> fsConfig) {
    fsConfig.orElseThrow(() -> new UnsupportedFileProviderException(FileProviderEnum.FS));
  }
}
