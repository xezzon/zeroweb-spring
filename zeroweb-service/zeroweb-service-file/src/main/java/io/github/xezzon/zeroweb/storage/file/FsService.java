package io.github.xezzon.zeroweb.storage.file;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.attachment.event.AttachmentCreatedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentDeletedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentUploadedEvent;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFsConfig;
import io.github.xezzon.zeroweb.common.constant.BannerConstant;
import io.github.xezzon.zeroweb.common.exception.IncorrectFileException;
import io.github.xezzon.zeroweb.common.exception.ReadFileException;
import io.github.xezzon.zeroweb.common.exception.WriteFileException;
import io.github.xezzon.zeroweb.storage.DownloadEndpoint;
import io.github.xezzon.zeroweb.storage.IStorageService;
import io.github.xezzon.zeroweb.storage.StorageContext;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/// @author xezzon
@Service
@ConditionalOnBean(ZerowebFsConfig.class)
@Slf4j
public class FsService implements IStorageService {

  static final String UPLOAD_ENDPOINT = "/fs/{id}/upload";
  static final String MULTIPART_UPLOAD_ENDPOINT = "/fs/{id}/upload/{partNumber}";
  static final String DOWNLOAD_ENDPOINT = "/fs/{id}/download";
  private static final Path TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir"))
      .resolve(BannerConstant.NAME);
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
  public UploadInfo getUploadInfo(Attachment attachment) {
    long partSize = zerowebFsConfig.getPartSize();
    int partCount = Math.toIntExact(
        (attachment.getSize() - 1) / partSize + 1);

    if (partCount > 1) {
      try {
        Files.createDirectories(TEMP_DIR.resolve(attachment.getId()));
      } catch (IOException e) {
        throw new WriteFileException(e);
      }
    }

    return new UploadInfo(
        attachment.getId(),
        attachment.getProvider(),
        partCount,
        partSize
    );
  }

  public UploadEndpoint getUploadAddress(Attachment attachment) {
    String endpoint = UriComponentsBuilder
        .fromPath(UPLOAD_ENDPOINT)
        .buildAndExpand(attachment.getId())
        .toUriString();
    return new UploadEndpoint(endpoint);
  }

  public UploadEndpoint getUploadAddress(Attachment attachment, int partNumber) {
    // 文件已存在，则跳过
    if (Files.exists(TEMP_DIR.resolve(attachment.getId()).resolve(String.valueOf(partNumber)))) {
      return new UploadEndpoint(partNumber);
    }

    String endpoint = UriComponentsBuilder
        .fromPath(MULTIPART_UPLOAD_ENDPOINT)
        .buildAndExpand(attachment.getId(), partNumber)
        .toUriString();
    return new UploadEndpoint(partNumber, endpoint);
  }

  @Override
  public DownloadEndpoint getDownloadEndpoint(Attachment attachment) {
    String endpoint = UriComponentsBuilder
        .fromPath(DOWNLOAD_ENDPOINT)
        .buildAndExpand(attachment.getId())
        .toUriString();
    return new DownloadEndpoint(
        endpoint
    );
  }

  @Override
  public void upload(Attachment attachment, byte[] fileContent) {
    Path path = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());
    // 校验哈希、大小
    if (!Objects.equals(fileContent.length, attachment.getSize().intValue())) {
      throw new IncorrectFileException("Invalid size.");
    }
    if (!Objects.equals(
        Base64.getEncoder().encodeToString(Hashing.sha256().hashBytes(fileContent).asBytes()),
        attachment.getChecksum())
    ) {
      throw new IncorrectFileException("Invalid checksum.");
    }

    try {
      // 递归创建其父目录
      Files.createDirectories(path.getParent());
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

  void upload(String id, byte[] fileContent) {
    Attachment attachment = attachmentService.queryById(id);
    this.upload(attachment, fileContent);
  }

  void upload(String id, int partNumber, byte[] fileContent) {
    Attachment attachment = attachmentService.queryById(id);
    if (attachment == null) {
      throw new IncorrectFileException("Invalid attachment.");
    }
    Path tempFile = TEMP_DIR
        .resolve(id)
        .resolve(String.valueOf(partNumber));
    try {
      Files.write(tempFile, fileContent, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new WriteFileException(e);
    }
  }

  byte[] download(String id) {
    Attachment attachment = attachmentService.queryById(id);
    Path path = zerowebFsConfig.getBasePath()
        .resolve(attachment.objectKey());
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new ReadFileException(e);
    }
  }

  /// 大文件上传前，需要新建 ID 同名的临时目录
  @EventListener
  void listen(AttachmentCreatedEvent event) {
    Attachment attachment = event.attachment();
    if (attachment.getProvider() != provider()) {
      return;
    }
    if (attachment.getSize() <= zerowebFsConfig.getPartSize()) {
      return;
    }
    if (!StorageContext.CRC.isBound()) {
      // gRPC 调用，无需开启分段上传
      return;
    }

    try {
      Files.createDirectories(TEMP_DIR.resolve(attachment.getId()));
    } catch (IOException e) {
      throw new WriteFileException(e);
    }
  }

  /// 大文件上传后，将分片合并
  @SuppressWarnings("UnstableApiUsage")
  @EventListener
  void listen(AttachmentUploadedEvent event) {
    Attachment attachment = event.attachment();
    if (attachment.getProvider() != provider()) {
      return;
    }
    if (attachment.getSize() <= zerowebFsConfig.getPartSize()) {
      return;
    }

    Path tempAttachmentDir = TEMP_DIR.resolve(attachment.getId());
    if (!tempAttachmentDir.toFile().exists()) {
      return;
    }

    Path mergedTemp;
    try {
      mergedTemp = Files.createTempFile("merge-", ".tmp");
    } catch (IOException e) {
      throw new WriteFileException("Fail to create  merge temporary file.", e);
    }

    try (
        Stream<Path> parts = Files.list(tempAttachmentDir);
        HashingOutputStream hashingStream = new HashingOutputStream(
            Hashing.sha256(),
            Files.newOutputStream(
                mergedTemp,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
        )
    ) {
      // 合并分段文件
      List<Path> partFiles = parts
          .filter(Files::isRegularFile)
          .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getFileName().toString())))
          .toList();
      long mergedSize = 0L;
      for (Path partFile : partFiles) {
        mergedSize += Files.copy(partFile, hashingStream);
      }
      hashingStream.flush();
      if (!Objects.equals(mergedSize, attachment.getSize())) {
        Files.deleteIfExists(mergedTemp);
        throw new IncorrectFileException("Invalid size.");
      }
      String checksum = Base64.getEncoder()
          .encodeToString(hashingStream.hash().asBytes());
      if (!Objects.equals(checksum, attachment.getChecksum())) {
        Files.deleteIfExists(mergedTemp);
        throw new IncorrectFileException("Invalid checksum.");
      }

      Path finalPath = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());
      Files.createDirectories(finalPath.getParent());
      Files.move(mergedTemp, finalPath, StandardCopyOption.REPLACE_EXISTING);
      File file = finalPath.toFile();
      file.setReadable(true, true);
      file.setWritable(true, true);
      file.setExecutable(false);
    } catch (IOException e) {
      try {
        Files.deleteIfExists(mergedTemp);
      } catch (IOException suppressed) {
        log.warn("Failed to clean merged temp file: {}", suppressed.getMessage());
      }
      throw new WriteFileException(e);
    } finally {
      // 清理临时文件
      try (Stream<Path> stream = Files.walk(tempAttachmentDir)) {
        stream
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      } catch (IOException e) {
        // Log the error but don't rethrow, as the main task is done
        log.warn("Failed to clean up temporary directory: {}", e.getMessage());
      }
    }
  }

  /// 附件删除后，将对应的文件也删除
  @EventListener
  @Async()
  void listen(AttachmentDeletedEvent event) throws IOException {
    final Attachment attachment = event.attachment();
    Path path = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());
    Files.deleteIfExists(path);
  }
}
