package io.github.xezzon.zeroweb.storage;

import lombok.Getter;

/// @author xezzon
@Getter
public class UploadEndpoint {

  /// 分段序号
  private int partNumber;
  /// 上传地址
  private String endpoint;
  /// 回调地址
  private String callback;

  @SuppressWarnings("unused")
  public UploadEndpoint() {
    super();
  }

  /// 单文件上传的地址
  /// @param endpoint 上传地址
  public UploadEndpoint(String endpoint) {
    this.partNumber = 0;
    this.endpoint = endpoint;
  }

  /// 已经上传过的分段
  /// @param partNumber 分段序号
  public UploadEndpoint(int partNumber) {
    this.partNumber = partNumber;
  }

  /// 无需回调的文件上传地址
  /// @param partNumber 分段序号
  /// @param endpoint 上传地址
  public UploadEndpoint(int partNumber, String endpoint) {
    this.partNumber = partNumber;
    this.endpoint = endpoint;
  }

  /// 需要回调的文件上传地址
  /// @param partNumber 分段序号
  /// @param endpoint 上传地址
  /// @param callback 回调地址
  public UploadEndpoint(int partNumber, String endpoint, String callback) {
    this.partNumber = partNumber;
    this.endpoint = endpoint;
    this.callback = callback;
  }
}
