package io.github.xezzon.zeroweb.storage.file;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.UploadAddress;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.storage.IStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/// @author xezzon
@Service
public class FsService implements IStorageService {

  public static final String UPLOAD_ENDPOINT = "/file/{id}/upload";

  @Override
  public FileProviderEnum provider() {
    return FileProviderEnum.FS;
  }

  @Override
  public UploadAddress getUploadAddress(Attachment attachment) {
    String endpoint = UriComponentsBuilder
        .fromPath(UPLOAD_ENDPOINT)
        .buildAndExpand(attachment.getId())
        .toUriString();
    return new UploadAddress(
        attachment.getId(),
        attachment.getProvider(),
        endpoint
    );
  }
}
