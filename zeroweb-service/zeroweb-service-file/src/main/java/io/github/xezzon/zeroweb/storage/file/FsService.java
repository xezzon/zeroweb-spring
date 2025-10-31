package io.github.xezzon.zeroweb.storage.file;

import com.google.common.hash.Hashing;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo.Address;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFsConfig;
import io.github.xezzon.zeroweb.common.exception.WriteFileException;
import io.github.xezzon.zeroweb.storage.IStorageService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/// @author xezzon
@Service
@ConditionalOnBean(ZerowebFsConfig.class)
public class FsService implements IStorageService {

  public static final String UPLOAD_ENDPOINT = "/file/{id}/upload";
  private final ZerowebFsConfig zerowebFsConfig;
  private final IAttachmentService attachmentService;

  public FsService(
      final ZerowebFsConfig zerowebFsConfig,
      @Lazy final IAttachmentService attachmentService
  ) {
    this.zerowebFsConfig = zerowebFsConfig;
    this.attachmentService = attachmentService;
  }

  @Override
  public FileProviderEnum provider() {
    return FileProviderEnum.FS;
  }

  @Override
  public Address getUploadAddress(Attachment attachment) {
    String endpoint = UriComponentsBuilder
        .fromPath(UPLOAD_ENDPOINT)
        .buildAndExpand(attachment.getId())
        .toUriString();
    return new Address(endpoint);
  }

  @Override
  public Address getUploadAddress(Attachment attachment, int partNumber) {
    throw new UnsupportedOperationException();
  }

  void upload(String id, byte[] fileContent) {
    Attachment attachment = attachmentService.queryById(id);
    Path path = zerowebFsConfig.getBasePath()
        .resolve(attachment.objectKey());
    try {
      // 递归创建其父目录
      Files.createDirectories(path.getParent());
      // 校验哈希、大小
      if (!Objects.equals(fileContent.length, attachment.getSize().intValue())) {
        throw new UploadFileException("Invalid size.");
      }
      if (!Objects.equals(
          Base64.getEncoder().encodeToString(Hashing.sha256().hashBytes(fileContent).asBytes()),
          attachment.getChecksum()
      )) {
        throw new UploadFileException("Invalid checksum.");
      }
      // 新建文件并设置其可访问性（所有者可读、可写，所有人不可执行）
      File file = path.toFile();
      file.setReadable(true, true);
      file.setWritable(true, true);
      file.setExecutable(false);
      file.createNewFile();
      // 写入文件内容
      Files.write(path, fileContent, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new WriteFileException(e);
    }
  }
}
