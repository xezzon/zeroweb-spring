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

  /// 存储类型
  FileProviderEnum provider();

  /// 获取上传元数据
  /// @param attachment 附件
  /// @return 上传元数据
  UploadInfo getUploadInfo(Attachment attachment);

  /// 获取附件的上传地址
  /// @param attachment 附件
  /// @return 上传地址
  UploadEndpoint getUploadAddress(Attachment attachment);

  /// 获取附件分段上传地址
  /// @param attachment 附件
  /// @param partNumber 分段序号
  /// @return 上传地址
  UploadEndpoint getUploadAddress(Attachment attachment, int partNumber);

  /// 获取附件下载地址
  /// @param attachment 附件信息
  /// @return 下载地址
  DownloadEndpoint getDownloadEndpoint(Attachment attachment);

  /// 上传文件分段
  /// 
  /// @param attachment 附件
  /// @param fileContent 文件内容
  void upload(Attachment attachment, byte[] fileContent);

  @Component
  class Factory {

    private final Map<FileProviderEnum, IStorageService> serviceMap;

    Factory(final List<IStorageService> storageServices) {
      this.serviceMap = storageServices.stream()
          .collect(Collectors.toMap(IStorageService::provider, Function.identity()));
    }

    public IStorageService get(FileProviderEnum provider) {
      IStorageService storageService = serviceMap.get(provider);
      if (storageService == null) {
        throw new UnsupportedFileProviderException(provider);
      }
      return storageService;
    }
  }
}
