package io.github.xezzon.zeroweb.storage;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.UploadAddress;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/// 文件存储抽象接口
/// @author xezzon
public interface IStorageService {

  /// 存储类型
  FileProviderEnum provider();

  /// 获取附件的上传地址
  /// @param attachment 附件
  /// @return 上传地址
  UploadAddress getUploadAddress(Attachment attachment);

  /**
   * 获取附件分段上传地址
   * @param attachment 附件
   * @param partNumber 分段序号
   * @return 上传地址
   */
  UploadAddress getUploadAddress(Attachment attachment, int partNumber);

  @Component
  class Factory {

    private final Map<FileProviderEnum, IStorageService> serviceMap;

    Factory(final List<IStorageService> storageServices) {
      this.serviceMap = storageServices.stream()
          .collect(Collectors.toMap(IStorageService::provider, Function.identity()));
    }

    public IStorageService get(FileProviderEnum provider) {
      return serviceMap.get(provider);
    }
  }
}
