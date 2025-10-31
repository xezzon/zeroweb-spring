package io.github.xezzon.zeroweb.storage.s3;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.exception.UnsupportedFileProviderException;
import io.github.xezzon.zeroweb.storage.s3.entity.S3Etag;
import java.util.Optional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xezzon
 */
@RestController
public class S3HttpEndpoint {

  private final S3Service s3Service;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public S3HttpEndpoint(Optional<S3Service> s3Service) {
    this.s3Service = s3Service.orElse(null);
  }

  /**
   * S3 上传分段后需要将 ETag 提交到服务器
   * @param id 附件ID
   */
  @PutMapping(S3Service.ETAG_CALLBACK_URL)
  public void upsertEtag(@RequestBody S3Etag etag, @PathVariable String id) {
    etag.setAttachmentId(id);
    s3Service().upsertEtag(etag);
  }

  private S3Service s3Service() {
    if (s3Service == null) {
      throw new UnsupportedFileProviderException(FileProviderEnum.S3);
    }
    return s3Service;
  }
}
