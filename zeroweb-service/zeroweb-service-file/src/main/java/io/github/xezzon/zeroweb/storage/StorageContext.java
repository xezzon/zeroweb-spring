package io.github.xezzon.zeroweb.storage;

/// 存储请求中包含的 CRC 信息。
///
/// 该信息仅 S3使用，不方便通过方法传递。
///
/// @author xezzon
public class StorageContext {

  public static final ScopedValue<String> CRC = ScopedValue.newInstance();

  private StorageContext() {
  }
}
