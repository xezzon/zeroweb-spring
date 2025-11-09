package io.github.xezzon.zeroweb.storage.file;

import com.google.common.hash.Hashing;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.attachment.event.AttachmentCreatedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentUploadedEvent;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.common.config.ZerowebFsConfig;
import io.github.xezzon.zeroweb.common.exception.WriteFileException;
import io.github.xezzon.zeroweb.storage.IStorageService;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/// @author xezzon
@Service
@ConditionalOnBean(ZerowebFsConfig.class)
@Slf4j
public class FsService implements IStorageService {

  static final String UPLOAD_ENDPOINT = "/fs/{id}/upload";
  static final String MULTIPART_UPLOAD_ENDPOINT = "/fs/{id}/upload/{partNumber}";
  private static final Path TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir"));
  private final ZerowebFsConfig zerowebFsConfig;
  private final ZerowebFileConfig zerowebFileConfig;
  private final IAttachmentService attachmentService;

  public FsService(
      final ZerowebFsConfig zerowebFsConfig,
      final ZerowebFileConfig zerowebFileConfig,
      @Lazy final IAttachmentService attachmentService
  ) {
    this.zerowebFsConfig = zerowebFsConfig;
    this.zerowebFileConfig = zerowebFileConfig;
    this.attachmentService = attachmentService;
  }

  @Override
  public FileProviderEnum provider() {
    return FileProviderEnum.FS;
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

  void upload(String id, byte[] fileContent) {
    Attachment attachment = attachmentService.queryById(id);
    Path path = zerowebFsConfig.getBasePath()
        .resolve(attachment.objectKey());
    try {
      // 校验哈希、大小
      if (!Objects.equals(fileContent.length, attachment.getSize().intValue())) {
        throw new IncorrectFileException("Invalid size.");
      }
      if (!Objects.equals(
          Base64.getEncoder().encodeToString(Hashing.sha256().hashBytes(fileContent).asBytes()),
          attachment.getChecksum()
      )) {
        throw new IncorrectFileException("Invalid checksum.");
      }
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

  void upload(String id, int partNumber, byte[] fileContent) {
    Path tempFile = TEMP_DIR
        .resolve(id)
        .resolve(String.valueOf(partNumber));
    try {
      Files.write(tempFile, fileContent, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new WriteFileException(e);
    }
  }

  @EventListener
  void listen(AttachmentCreatedEvent event) {
    Attachment attachment = event.attachment();
    if (attachment.getProvider() != provider()) {
      return;
    }
    if (attachment.getSize() <= zerowebFileConfig.getMaxPartSize()) {
      return;
    }

    try {
      Files.createDirectories(TEMP_DIR.resolve(attachment.getId()));
    } catch (IOException e) {
      throw new WriteFileException(e);
    }
  }

  @EventListener
  void listen(AttachmentUploadedEvent event) {
    Attachment attachment = event.attachment();
    if (attachment.getProvider() != provider()) {
      return;
    }
    if (attachment.getSize() <= zerowebFileConfig.getMaxPartSize()) {
      return;
    }

    Path tempAttachmentDir = TEMP_DIR.resolve(attachment.getId());
    Path finalPath = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());

    try (
        Stream<Path> parts = Files.list(tempAttachmentDir);
        ByteArrayOutputStream mergedFileStream = new ByteArrayOutputStream()
    ) {
      // 合并分段文件
      List<Path> partFiles = parts
          .filter(Files::isRegularFile)
          .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getFileName().toString())))
          .toList();
      for (Path partFile : partFiles) {
        Files.copy(partFile, mergedFileStream);
      }

      // 校验哈希、大小
      byte[] mergedFileContent = mergedFileStream.toByteArray();
      if (!Objects.equals(mergedFileContent.length, attachment.getSize().intValue())) {
        throw new IncorrectFileException("Invalid size.");
      }
      if (!Objects.equals(
          Base64.getEncoder()
              .encodeToString(Hashing.sha256().hashBytes(mergedFileContent).asBytes()),
          attachment.getChecksum()
      )) {
        throw new IncorrectFileException("Invalid checksum.");
      }

      // 递归创建其父目录
      Files.createDirectories(finalPath.getParent());
      // 设置文件可访问性
      File file = finalPath.toFile();
      file.setReadable(true, true);
      file.setWritable(true, true);
      file.setExecutable(false);
      file.createNewFile();
      // 写入文件内容
      Files.write(finalPath, mergedFileContent, StandardOpenOption.CREATE);

    } catch (IOException e) {
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
}
