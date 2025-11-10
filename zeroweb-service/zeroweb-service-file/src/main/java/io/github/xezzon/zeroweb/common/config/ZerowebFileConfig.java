package io.github.xezzon.zeroweb.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/// 文件相关配置
/// @author xezzon
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = ZerowebFileConfig.PREFIX)
public class ZerowebFileConfig {

  public static final String PREFIX = ZerowebConfig.ZEROWEB + ".file";

  /// 存储后端
  private FileProviderEnum provider = FileProviderEnum.FS;
}
