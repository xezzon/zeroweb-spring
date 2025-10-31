package io.github.xezzon.zeroweb.attachment.entity;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import java.util.List;
import lombok.Getter;

/// 文件上传信息
/// @param id 附件ID
/// @param provider 存储提供商
/// @param addresses 上传地址
/// @param partCount 分片数量
/// @param partSize 分片大小。单位 Byte。
/// @author xezzon
public record UploadInfo(
    String id,
    FileProviderEnum provider,
    List<Address> addresses,
    int partCount,
    int partSize
) {

  @Getter
  public static class Address {

    /**
     * 分段序号
     */
    private int partNumber;
    /**
     * 上传地址
     */
    private String endpoint;
    /**
     * 回调地址
     */
    private String callback;

    @SuppressWarnings("unused")
    public Address() {
      super();
    }

    public Address(String endpoint) {
      this.partNumber = 1;
      this.endpoint = endpoint;
    }

    public Address(int partNumber, String endpoint) {
      this.partNumber = partNumber;
      this.endpoint = endpoint;
    }

    public Address(int partNumber, String endpoint, String callback) {
      this.partNumber = partNumber;
      this.endpoint = endpoint;
      this.callback = callback;
    }
  }
}
