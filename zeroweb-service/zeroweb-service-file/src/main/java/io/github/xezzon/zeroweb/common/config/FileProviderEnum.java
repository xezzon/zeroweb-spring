package io.github.xezzon.zeroweb.common.config;

/// 存储后端
/// @author xezzon
public enum FileProviderEnum {
  /// 硬盘存储
  FS,
  /// 对象存储（兼容 S3）
  S3,
}
