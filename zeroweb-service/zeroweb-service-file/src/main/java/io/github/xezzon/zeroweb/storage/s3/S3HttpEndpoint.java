package io.github.xezzon.zeroweb.storage.s3;

import io.github.xezzon.zeroweb.storage.s3.entity.S3Etag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
@RequestMapping("/s3")
public class S3HttpEndpoint {

  private final S3Service s3Service;

  public S3HttpEndpoint(S3Service s3Service) {
    this.s3Service = s3Service;
  }

  /**
   * S3 上传分段后需要将 ETag 提交到服务器
   * @param id 附件ID
   */
  @PutMapping("/{id}/etag")
  public void upsertEtag(@RequestBody S3Etag etag, @PathVariable String id) {
    etag.setAttachmentId(id);
    s3Service.upsertEtag(etag);
  }
}
