package io.github.xezzon.zeroweb.storage.file;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/// @author xezzon
@RestController
public class FsHttpEndpoint {

  private final FsService fsService;

  public FsHttpEndpoint(FsService fsService) {
    this.fsService = fsService;
  }

  /// 上传文件到服务器磁盘
  /// @param id 附件ID
  /// @param fileContent 文件内容
  @PutMapping(FsService.UPLOAD_ENDPOINT)
  public void upload(@PathVariable String id, @RequestBody byte[] fileContent) {
    fsService.upload(id, fileContent);
  }
}
