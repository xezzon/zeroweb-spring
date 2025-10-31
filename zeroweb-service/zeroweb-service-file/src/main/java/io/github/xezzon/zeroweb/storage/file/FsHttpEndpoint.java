package io.github.xezzon.zeroweb.storage.file;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import java.util.Optional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/// @author xezzon
@RestController
public class FsHttpEndpoint {

  private final FsService fsService;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public FsHttpEndpoint(Optional<FsService> fsService) {
    this.fsService = fsService.orElse(null);
  }

  /// 上传文件到服务器磁盘
  /// @param id 附件ID
  /// @param fileContent 文件内容
  @PutMapping(FsService.UPLOAD_ENDPOINT)
  public void upload(@PathVariable String id, @RequestBody byte[] fileContent) {
    fsService().upload(id, fileContent);
  }

  private FsService fsService() {
    if (fsService == null) {
      throw new UnsupportedFileProviderException(FileProviderEnum.FS);
    }
    return fsService;
  }
}
