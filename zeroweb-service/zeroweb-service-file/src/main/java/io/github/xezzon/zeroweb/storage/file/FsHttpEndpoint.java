package io.github.xezzon.zeroweb.storage.file;

import io.github.xezzon.zeroweb.common.config.ZerowebFsConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import java.util.Optional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/// @author xezzon
@RestController
@ConditionalOnBean(ZerowebFsConfig.class)
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

  /**
   * 上传文件分段到服务器磁盘
   * @param id 附件ID
   * @param partNumber 分段序号
   * @param fileContent 文件内容
   */
  @PutMapping(FsService.MULTIPART_UPLOAD_ENDPOINT)
  public void upload(
      @PathVariable final String id,
      @PathVariable final int partNumber,
      @RequestBody byte[] fileContent
  ) {
    fsService.upload(id, partNumber, fileContent);
  }
}
