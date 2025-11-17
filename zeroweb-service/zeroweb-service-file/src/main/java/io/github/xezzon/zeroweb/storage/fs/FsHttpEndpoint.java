package io.github.xezzon.zeroweb.storage.fs;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/// @author xezzon
@RestController
@ConditionalOnBean(ZerowebFsConfig.class)
public class FsHttpEndpoint {

  private final FsService fsService;
  private final IAttachmentService attachmentService;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public FsHttpEndpoint(
      final Optional<FsService> fsService,
      final IAttachmentService attachmentService
  ) {
    this.fsService = fsService.orElse(null);
    this.attachmentService = attachmentService;
  }

  /// 上传文件到服务器磁盘
  /// 
  /// @param id          附件ID
  /// @param fileContent 文件内容
  @PutMapping(FsService.UPLOAD_ENDPOINT)
  public void upload(@PathVariable String id, @RequestBody byte[] fileContent) {
    fsService().upload(id, fileContent);
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
    fsService().upload(id, partNumber, fileContent);
  }

  /// 下载文件
  /// 
  /// @param id 附件ID
  @GetMapping(FsService.DOWNLOAD_ENDPOINT)
  public ResponseEntity<byte[]> download(@PathVariable String id) {
    Attachment attachment = attachmentService.queryById(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(attachment.getType()));
    byte[] fileContent = fsService().download(id);
    return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
  }

  private FsService fsService() {
    if (fsService == null) {
      throw new UnsupportedFileProviderException(FileProviderEnum.FS);
    }
    return fsService;
  }
}
