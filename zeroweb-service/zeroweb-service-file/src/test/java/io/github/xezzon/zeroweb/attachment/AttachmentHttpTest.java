package io.github.xezzon.zeroweb.attachment;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import io.floci.testcontainers.FlociContainer;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentReq;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.common.constant.BannerConstant;
import io.github.xezzon.zeroweb.common.domain.PagedModel;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.common.exception.ReadFileException;
import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import io.github.xezzon.zeroweb.storage.DownloadEndpoint;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
import io.github.xezzon.zeroweb.storage.fs.ZerowebFsConfig;
import io.github.xezzon.zeroweb.storage.s3.ZerowebS3Config;
import io.github.xezzon.zeroweb.storage.s3.repository.S3UploadIdRepository;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
abstract class AttachmentHttpTest {

  private static final String ADD_ATTACHMENT = "/attachment";
  private static final String GET_UPLOAD_INFO = "/attachment/{id}/resume";
  private static final String GET_UPLOAD_ADDRESS = "/attachment/{id}/endpoint/upload";
  private static final String FINISH_UPLOAD = "/attachment/{id}/status/done";
  private static final String QUERY_BY_BIZ = "/attachment/list";
  private static final String QUERY_BY_ID = "/attachment/{id}";
  private static final String GET_DOWNLOAD_ADDRESS = "/attachment/{id}/endpoint/download";
  private static final String DELETE_ATTACHMENT = "/attachment/{id}";
  private static final String QUERY_PAGE = "/attachment/page";
  private static final String FILE_NAME = "test.txt";
  private static final String LARGE_FILE = "large_file.jpg";

  final Path resource = ResourceUtil.getResourceFromClasspath(FILE_NAME);
  private final Path largeFileResource = ResourceUtil.getResourceFromClasspath(LARGE_FILE);
  private final Attachment attachment = new Attachment();
  private Attachment largeFileAttachment = new Attachment();
  private final Attachment attachmentForDownloading = new Attachment();
  private final List<byte[]> largeFileParts = new ArrayList<>();

  @Resource
  private RestTestClient testClient;
  @Resource
  AttachmentRepository repository;
  @Resource
  private ZerowebFileConfig zerowebFileConfig;
  @LocalServerPort
  private int port;

  abstract long partSize();

  abstract byte[] readFile(Attachment attachment);

  abstract void assertIncorrectFileStatus(int status);

  abstract void expire(Attachment attachment);

  abstract void saveFile(Attachment attachment) throws IOException;

  @BeforeEach
  void setUp_file() throws Exception {
    File file = resource.toFile();
    attachment.setName(FILE_NAME);
    attachment.setChecksum(Base64.getEncoder().encodeToString(
        Hashing.sha256()
            .hashBytes(Files.readAllBytes(resource))
            .asBytes()
    ));
    attachment.setSize(file.length());
    attachment.setType(Files.probeContentType(resource));
    attachment.setBizType(RandomUtil.randomString(8));
    attachment.setBizId(UUID.randomUUID().toString());
    attachment.setProvider(zerowebFileConfig.getProvider());
    attachment.setStatus(AttachmentStatusEnum.UPLOADING);
    repository.save(attachment);
  }

  @BeforeEach
  void setUp_largeFile() throws IOException {
    ByteSource byteSource = ByteSource.wrap(Files.readAllBytes(largeFileResource));
    long offset = 0;
    while (offset < byteSource.size()) {
      long length = Math.min(this.partSize(), byteSource.size() - offset);
      largeFileParts.add(byteSource.slice(offset, length).read());
      offset += length;
    }

    File largeFile = largeFileResource.toFile();
    largeFileAttachment.setName(LARGE_FILE);
    largeFileAttachment.setChecksum(Base64.getEncoder().encodeToString(
        Hashing.sha256()
            .hashBytes(byteSource.read())
            .asBytes()
    ));
    largeFileAttachment.setSize(largeFile.length());
    largeFileAttachment.setType(Files.probeContentType(largeFileResource));
    largeFileAttachment.setBizType(RandomUtil.randomString(8));
    largeFileAttachment.setBizId(UUID.randomUUID().toString());
    largeFileAttachment.setProvider(zerowebFileConfig.getProvider());
    largeFileAttachment.setStatus(AttachmentStatusEnum.UPLOADING);
  }

  @BeforeEach
  void setUp_download() throws IOException {
    File file = resource.toFile();
    attachmentForDownloading.setName(FILE_NAME);
    attachmentForDownloading.setChecksum(Base64.getEncoder().encodeToString(
        Hashing.sha256()
            .hashBytes(Files.readAllBytes(resource))
            .asBytes()
    ));
    attachmentForDownloading.setSize(file.length());
    attachmentForDownloading.setType(Files.probeContentType(resource));
    attachmentForDownloading.setBizType(RandomUtil.randomString(8));
    attachmentForDownloading.setBizId(UUID.randomUUID().toString());
    attachmentForDownloading.setProvider(zerowebFileConfig.getProvider());
    attachmentForDownloading.setStatus(AttachmentStatusEnum.DONE);
    repository.save(attachmentForDownloading);
    this.saveFile(attachmentForDownloading);
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
    largeFileParts.clear();
  }

  @Test
  void addAttachment() {
    final String userId = UUID.randomUUID().toString();

    AddAttachmentReq req = new AddAttachmentReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(44),
        RandomUtil.randomLong(5) * 1024 * 1024 + 1,
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        UUID.randomUUID().toString()
    );
    UploadInfo responseBody = testClient.post()
        .uri(ADD_ATTACHMENT)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(userId).bearer())
        .body(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(
        (req.size() - 1) / (this.partSize()) + 1,
        responseBody.partCount()
    );
    Attachment actual = repository.findById(responseBody.id()).orElseThrow();
    Assertions.assertEquals(zerowebFileConfig.getProvider(), actual.getProvider());
    Assertions.assertEquals(AttachmentStatusEnum.UPLOADING, actual.getStatus());
    Assertions.assertEquals(userId, actual.getOwnerId());
  }

  @Test
  void addAttachment_notLogin() {
    AddAttachmentReq req = new AddAttachmentReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(44),
        RandomUtil.randomLong(5) * 1024 * 1024 + 1,
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        UUID.randomUUID().toString()
    );
    UploadInfo responseBody = testClient.post()
        .uri(ADD_ATTACHMENT)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .body(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Attachment actual = repository.findById(responseBody.id()).orElseThrow();
    Assertions.assertNull(actual.getOwnerId());
  }

  @Test
  void upload() throws IOException {
    UploadInfo uploadInfo = testClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_INFO)
            .queryParam("checksum", attachment.getChecksum())
            .queryParam("fileSize", attachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(uploadInfo);
    Assertions.assertEquals(1, uploadInfo.partCount());

    UploadEndpoint uploadEndpoint = testClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_ADDRESS)
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadEndpoint.class).returnResult().getResponseBody();
    Assertions.assertNotNull(uploadEndpoint);

    byte[] fileContent = Files.readAllBytes(resource);
    testClient.put()
        .uri(localhost(uploadEndpoint.getEndpoint()))
        .body(fileContent)
        .header("Content-Type", Files.probeContentType(resource))
        .header("x-amz-meta-filename", attachment.getName())
        .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
        .header("x-amz-checksum-sha256", attachment.getChecksum())
        .exchange()
        .expectStatus().isOk();
    Assertions.assertArrayEquals(fileContent, readFile(attachment));
  }

  @Test
  void upload_largeFile() throws IOException {
    // 新增附件
    AddAttachmentReq req = new AddAttachmentReq(
        largeFileAttachment.getName(),
        largeFileAttachment.getChecksum(),
        largeFileAttachment.getSize(),
        largeFileAttachment.getType(),
        largeFileAttachment.getBizType(),
        largeFileAttachment.getBizId()
    );
    CRC32 crc32 = new CRC32();
    crc32.update(Files.readAllBytes(largeFileResource));
    String crc = Base64.getEncoder().encodeToString(
        HexUtil.decodeHex(Long.toHexString(crc32.getValue()))
    );
    UploadInfo uploadInfo = testClient.post()
        .uri(builder -> builder
            .path(ADD_ATTACHMENT)
            .queryParam("crc", crc)
            .build()
        )
        .body(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(uploadInfo);
    Assertions.assertTrue(uploadInfo.partCount() > 1);
    final String attachmentId = uploadInfo.id();
    largeFileAttachment = repository.findById(attachmentId).orElseThrow();

    // 上传其中一个分片
    {
      final byte[] partContent = largeFileParts.get(1);
      final CRC32 partCrc32 = new CRC32();
      partCrc32.update(partContent);
      final String partChecksum = Base64.getEncoder().encodeToString(
          HexUtil.decodeHex(Long.toHexString(partCrc32.getValue()))
      );
      UploadEndpoint address = testClient.get()
          .uri(builder -> builder
              .path(GET_UPLOAD_ADDRESS)
              .queryParam("partNumber", 2)
              .queryParam("crc", partChecksum)
              .build(largeFileAttachment.getId())
          )
          .exchange()
          .expectStatus().isOk()
          .expectBody(UploadEndpoint.class).returnResult().getResponseBody();
      Assertions.assertNotNull(address);
      URI uri = localhost(address.getEndpoint());
      testClient.put()
          .uri(uri)
          .body(partContent)
          .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.CRC32.toString())
          .header("x-amz-checksum-crc32", partChecksum)
          .exchange()
          .expectStatus().isOk();
      // 重复上传，测试幂等性
      testClient.put()
          .uri(uri)
          .body(partContent)
          .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.CRC32.toString())
          .header("x-amz-checksum-crc32", partChecksum)
          .exchange()
          .expectStatus().isOk();
    }

    // 测试断点续传
    {
      uploadInfo = testClient.get()
          .uri(builder -> builder
              .path(GET_UPLOAD_INFO)
              .queryParam("checksum", largeFileAttachment.getChecksum())
              .queryParam("fileSize", largeFileAttachment.getSize())
              .build(attachmentId)
          )
          .exchange()
          .expectStatus().isOk()
          .expectBody(UploadInfo.class)
          .returnResult().getResponseBody();
      Assertions.assertNotNull(uploadInfo);

      IntStream.range(1, uploadInfo.partCount() + 1)
          .forEach(partNumber -> {
            final byte[] partContent = largeFileParts.get(partNumber - 1);
            final CRC32 partCrc32 = new CRC32();
            partCrc32.update(partContent);
            final String partChecksum = Base64.getEncoder().encodeToString(
                HexUtil.decodeHex(Long.toHexString(partCrc32.getValue()))
            );
            UploadEndpoint address = testClient.get()
                .uri(builder -> builder
                    .path(GET_UPLOAD_ADDRESS)
                    .queryParam("partNumber", partNumber)
                    .queryParam("crc", partChecksum)
                    .build(attachmentId)
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(UploadEndpoint.class).returnResult().getResponseBody();
            Assertions.assertNotNull(address);
            if (partNumber == 2) {
              Assertions.assertNull(address.getEndpoint());
              return;
            }

            final URI uri = localhost(address.getEndpoint());
            testClient.put()
                .uri(uri)
                .body(partContent)
                .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.CRC32.toString())
                .header("x-amz-checksum-crc32", partChecksum)
                .exchange()
                .expectStatus().isOk();
          });
    }

    testClient.put()
        .uri(FINISH_UPLOAD, attachmentId)
        .exchange()
        .expectStatus().isOk();
    Assertions.assertArrayEquals(
        Files.readAllBytes(largeFileResource),
        readFile(largeFileAttachment)
    );
  }

  @Test
  void upload_incorrectChecksum() {
    testClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_INFO)
            .queryParam("checksum", largeFileAttachment.getChecksum())
            .queryParam("fileSize", attachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().is4xxClientError();

    testClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_INFO)
            .queryParam("checksum", attachment.getChecksum())
            .queryParam("fileSize", largeFileAttachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().is4xxClientError();

    testClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_INFO)
            .queryParam("checksum", largeFileAttachment.getChecksum())
            .queryParam("fileSize", largeFileAttachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  void upload_incorrectFile() throws IOException {
    UploadInfo responseBody = testClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_INFO)
            .queryParam("checksum", attachment.getChecksum())
            .queryParam("fileSize", attachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);

    Path path = ResourceUtil.getResourceFromClasspath("incorrect_file.txt");

    UploadEndpoint address = testClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_ADDRESS)
            .build(responseBody.id())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadEndpoint.class).returnResult().getResponseBody();
    Assertions.assertNotNull(address);
    testClient.put()
        .uri(localhost(address.getEndpoint()))
        .body(Files.readAllBytes(path))
        .header("Content-Type", Files.probeContentType(path))
        .header("x-amz-meta-filename", attachment.getName())
        .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
        .header("x-amz-checksum-sha256", attachment.getChecksum())
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  void upload_incorrectLargeFile() throws IOException {
    Path path = ResourceUtil.getResourceFromClasspath("incorrect_large_file.jpg");
    ByteSource byteSource = ByteSource.wrap(Files.readAllBytes(path));
    long offset = 0;
    List<byte[]> incorrectParts = new ArrayList<>();
    while (offset < byteSource.size()) {
      long length = Math.min(this.partSize(), byteSource.size() - offset);
      incorrectParts.add(byteSource.slice(offset, length).read());
      offset += length;
    }
    // 新增附件
    AddAttachmentReq req = new AddAttachmentReq(
        largeFileAttachment.getName(),
        largeFileAttachment.getChecksum(),
        largeFileAttachment.getSize(),
        largeFileAttachment.getType(),
        largeFileAttachment.getBizType(),
        largeFileAttachment.getBizId()
    );
    CRC32 crc32 = new CRC32();
    crc32.update(Files.readAllBytes(largeFileResource));
    final String checksum = Base64.getEncoder().encodeToString(
        HexUtil.decodeHex(Long.toHexString(crc32.getValue()))
    );
    UploadInfo responseBody = testClient.post()
        .uri(builder -> builder
            .path(ADD_ATTACHMENT)
            .queryParam("crc", checksum)
            .build()
        )
        .body(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(responseBody.partCount() > 1);
    final String attachmentId = responseBody.id();
    largeFileAttachment = repository.findById(attachmentId).orElseThrow();

    IntStream.range(1, incorrectParts.size() + 1)
        .forEach(partNumber -> {
          final byte[] partContent = incorrectParts.get(partNumber - 1);
          final CRC32 partCrc32 = new CRC32();
          partCrc32.update(partContent);
          final String partChecksum = Base64.getEncoder().encodeToString(
              HexUtil.decodeHex(Long.toHexString(partCrc32.getValue()))
          );
          UploadEndpoint address = testClient.get()
              .uri(builder -> builder
                  .path(GET_UPLOAD_ADDRESS)
                  .queryParam("partNumber", partNumber)
                  .queryParam("crc", partChecksum)
                  .build(attachmentId)
              )
              .exchange()
              .expectStatus().isOk()
              .expectBody(UploadEndpoint.class).returnResult().getResponseBody();
          Assertions.assertNotNull(address);
          final URI uri = localhost(address.getEndpoint());

          testClient.put()
              .uri(uri)
              .body(partContent)
              .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.CRC32.toString())
              .header("x-amz-checksum-crc32", partChecksum)
              .exchange()
              .expectStatus().isOk();
        });

    testClient.put()
        .uri(FINISH_UPLOAD, attachmentId)
        .exchange()
        .expectStatus().value(this::assertIncorrectFileStatus);
  }

  @Test
  void upload_incorrectPartSize() throws IOException {
    ByteSource byteSource = ByteSource.wrap(Files.readAllBytes(largeFileResource));
    long offset = 0;
    long incorrectPartSize = this.partSize() + 1024 * 1024;
    List<byte[]> incorrectParts = new ArrayList<>();
    while (offset < byteSource.size()) {
      long length = Math.min(incorrectPartSize, byteSource.size() - offset);
      incorrectParts.add(byteSource.slice(offset, length).read());
      offset += length;
    }
    CRC32 crc32 = new CRC32();
    crc32.update(byteSource.read());
    final String checksum = Base64.getEncoder().encodeToString(
        HexUtil.decodeHex(Long.toHexString(crc32.getValue()))
    );
    // 新增附件
    AddAttachmentReq req = new AddAttachmentReq(
        largeFileAttachment.getName(),
        largeFileAttachment.getChecksum(),
        largeFileAttachment.getSize(),
        largeFileAttachment.getType(),
        largeFileAttachment.getBizType(),
        largeFileAttachment.getBizId()
    );
    UploadInfo responseBody = testClient.post()
        .uri(builder -> builder
            .path(ADD_ATTACHMENT)
            .queryParam("crc", checksum)
            .build()
        )
        .body(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(responseBody.partCount() > 1);
    final String attachmentId = responseBody.id();
    largeFileAttachment = repository.findById(attachmentId).orElseThrow();

    IntStream.range(1, incorrectParts.size() + 1)
        .forEach(partNumber -> {
          final byte[] partContent = incorrectParts.get(partNumber - 1);
          final CRC32 partCrc32 = new CRC32();
          partCrc32.update(partContent);
          final String partChecksum = Base64.getEncoder().encodeToString(
              HexUtil.decodeHex(Long.toHexString(partCrc32.getValue()))
          );
          UploadEndpoint address = testClient.get()
              .uri(builder -> builder
                  .path(GET_UPLOAD_ADDRESS)
                  .queryParam("partNumber", partNumber)
                  .queryParam("crc", partChecksum)
                  .build(attachmentId)
              )
              .exchange()
              .expectStatus().isOk()
              .expectBody(UploadEndpoint.class).returnResult().getResponseBody();
          Assertions.assertNotNull(address);
          final URI uri = localhost(address.getEndpoint());
          testClient.put()
              .uri(uri)
              .body(partContent)
              .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.CRC32.toString())
              .header("x-amz-checksum-crc32", partChecksum)
              .exchange()
              .expectStatus().isOk();
        });

    testClient.put()
        .uri(FINISH_UPLOAD, attachmentId)
        .exchange()
        .expectStatus().isOk();
    Assertions.assertArrayEquals(
        byteSource.read(),
        readFile(largeFileAttachment)
    );
  }

  @Test
  void upload_uploadIdExpiration() throws IOException {
    // 新增附件
    AddAttachmentReq req = new AddAttachmentReq(
        largeFileAttachment.getName(),
        largeFileAttachment.getChecksum(),
        largeFileAttachment.getSize(),
        largeFileAttachment.getType(),
        largeFileAttachment.getBizType(),
        largeFileAttachment.getBizId()
    );
    CRC32 crc32 = new CRC32();
    crc32.update(Files.readAllBytes(largeFileResource));
    String crc = Base64.getEncoder().encodeToString(
        HexUtil.decodeHex(Long.toHexString(crc32.getValue()))
    );
    UploadInfo uploadInfo = testClient.post()
        .uri(builder -> builder
            .path(ADD_ATTACHMENT)
            .queryParam("crc", crc)
            .build()
        )
        .body(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(uploadInfo);
    Assertions.assertTrue(uploadInfo.partCount() > 1);
    final String attachmentId = uploadInfo.id();
    largeFileAttachment = repository.findById(attachmentId).orElseThrow();

    // 上传其中一个分片
    {
      final byte[] partContent = largeFileParts.get(1);
      final CRC32 partCrc32 = new CRC32();
      partCrc32.update(partContent);
      final String partChecksum = Base64.getEncoder().encodeToString(
          HexUtil.decodeHex(Long.toHexString(partCrc32.getValue()))
      );
      UploadEndpoint address = testClient.get()
          .uri(builder -> builder
              .path(GET_UPLOAD_ADDRESS)
              .queryParam("partNumber", 2)
              .queryParam("crc", partChecksum)
              .build(largeFileAttachment.getId())
          )
          .exchange()
          .expectStatus().isOk()
          .expectBody(UploadEndpoint.class).returnResult().getResponseBody();
      Assertions.assertNotNull(address);
      URI uri = localhost(address.getEndpoint());
      testClient.put()
          .uri(uri)
          .body(partContent)
          .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.CRC32.toString())
          .header("x-amz-checksum-crc32", partChecksum)
          .exchange()
          .expectStatus().isOk();
    }

    // 模拟分段过期
    this.expire(largeFileAttachment);

    // 测试断点续传
    {
      uploadInfo = testClient.get()
          .uri(builder -> builder
              .path(GET_UPLOAD_INFO)
              .queryParam("checksum", largeFileAttachment.getChecksum())
              .queryParam("fileSize", largeFileAttachment.getSize())
              .build(attachmentId)
          )
          .exchange()
          .expectStatus().isOk()
          .expectBody(UploadInfo.class)
          .returnResult().getResponseBody();
      Assertions.assertNotNull(uploadInfo);

      for (int i = 1; i <= uploadInfo.partCount(); i++) {
        int partNumber = i;
        final byte[] partContent = largeFileParts.get(partNumber - 1);
        final CRC32 partCrc32 = new CRC32();
        partCrc32.update(partContent);
        final String partChecksum = Base64.getEncoder().encodeToString(
            HexUtil.decodeHex(Long.toHexString(partCrc32.getValue()))
        );
        UploadEndpoint address = testClient.get()
            .uri(builder -> builder
                .path(GET_UPLOAD_ADDRESS)
                .queryParam("partNumber", partNumber)
                .queryParam("crc", partChecksum)
                .build(attachmentId)
            )
            .exchange()
            .expectStatus().isOk()
            .expectBody(UploadEndpoint.class).returnResult().getResponseBody();
        Assertions.assertNotNull(address);
        Assertions.assertNotNull(address.getEndpoint());

        final URI uri = localhost(address.getEndpoint());
        testClient.put()
            .uri(uri)
            .body(partContent)
            .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.CRC32.toString())
            .header("x-amz-checksum-crc32", partChecksum)
            .exchange()
            .expectStatus().isOk();
      }
    }

    testClient.put()
        .uri(FINISH_UPLOAD, attachmentId)
        .exchange()
        .expectStatus().isOk();
    Assertions.assertArrayEquals(
        Files.readAllBytes(largeFileResource),
        readFile(largeFileAttachment)
    );
  }

  @Test
  void finishUpload() {
    Attachment before = repository.findById(attachment.getId()).orElseThrow();
    Assertions.assertEquals(AttachmentStatusEnum.UPLOADING, before.getStatus());

    testClient.put()
        .uri(FINISH_UPLOAD, attachment.getId())
        .exchange()
        .expectStatus().isOk();

    Attachment after = repository.findById(attachment.getId()).orElseThrow();
    Assertions.assertEquals(AttachmentStatusEnum.DONE, after.getStatus());
  }

  @Test
  void queryByBiz() {
    List<Attachment> responseBody = testClient.get()
        .uri(uri -> uri.path(QUERY_BY_BIZ)
            .queryParam("bizType", attachment.getBizType())
            .queryParam("bizId", attachment.getBizId())
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull List<@NonNull Attachment>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);

    Assertions.assertEquals(1, responseBody.size());
    Assertions.assertEquals(attachment.getId(), responseBody.getFirst().getId());
  }

  @Test
  void queryByBiz_empty() {
    List<Attachment> responseBody = testClient.get()
        .uri(uri -> uri.path(QUERY_BY_BIZ)
            .queryParam("bizType", RandomUtil.randomString(6))
            .queryParam("bizId", attachment.getBizId())
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull List<@NonNull Attachment>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(responseBody.isEmpty());

    List<Attachment> responseBody2 = testClient.get()
        .uri(uri -> uri.path(QUERY_BY_BIZ)
            .queryParam("bizType", attachment.getBizType())
            .queryParam("bizId", UUID.randomUUID().toString())
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull List<Attachment>>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody2);
    Assertions.assertTrue(responseBody2.isEmpty());
  }

  @Test
  void queryById() {
    Attachment responseBody = testClient.get()
        .uri(QUERY_BY_ID, attachment.getId())
        .exchange()
        .expectStatus().isOk()
        .expectBody(Attachment.class).returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(attachment.getId(), responseBody.getId());
  }

  @Test
  void download() throws IOException {
    DownloadEndpoint downloadEndpoint = testClient.get()
        .uri(uri -> uri
            .path(GET_DOWNLOAD_ADDRESS)
            .build(attachmentForDownloading.getId())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(DownloadEndpoint.class).returnResult().getResponseBody();
    Assertions.assertNotNull(downloadEndpoint);

    byte[] fileContent = testClient.get()
        .uri(localhost(downloadEndpoint.getEndpoint()))
        .exchange()
        .expectStatus().isOk()
        .expectHeader().valueEquals("Content-Type", attachment.getType())
        .expectBody(new ParameterizedTypeReference<byte @NonNull []>() {
        })
        .returnResult().getResponseBody();
    Assertions.assertNotNull(fileContent);
    byte[] expect = Files.readAllBytes(resource);
    Assertions.assertArrayEquals(expect, fileContent);
  }

  @Test
  void deleteAttachment() {
    testClient.delete()
        .uri(builder -> builder
            .path(DELETE_ATTACHMENT)
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().isOk();
    Assertions.assertFalse(repository.existsById(attachment.getId()));
    Assertions.assertArrayEquals(new byte[0], this.readFile(attachment));
  }

  @Test
  void queryPage() {
    final int top = 5;
    final int skip = 0;

    PagedModel<Attachment> responseBody = testClient.get()
        .uri(builder -> builder
            .path(QUERY_PAGE)
            .queryParam("top", top)
            .queryParam("skip", skip)
            .build()
        )
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().bearer())
        .exchange()
        .expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<@NonNull PagedModel<Attachment>>() {
        })
        .returnResult().getResponseBody();

    assertNotNull(responseBody);
    Assertions.assertEquals(2, responseBody.getPage().getTotalElements());
    List<Attachment> actual = responseBody.getContent();
    Assertions.assertTrue(actual.stream().anyMatch(o ->
        Objects.equals(o.getId(), attachment.getId())
    ));
    Assertions.assertTrue(actual.stream().anyMatch(o ->
        Objects.equals(o.getId(), attachmentForDownloading.getId())
    ));
  }

  private URI localhost(String uri) {
    return URI.create("http://localhost:" + port).resolve(URI.create(uri));
  }
}

@ActiveProfiles("s3")
class S3HttpTest extends AttachmentHttpTest {

  private static final FlociContainer CONTAINER = new FlociContainer();
  private static final String BUCKET = "test";
  private static S3Client s3Client = null;
  @Resource
  private ZerowebS3Config zerowebS3Config;
  @Resource
  private S3UploadIdRepository s3UploadIdRepository;

  @BeforeAll
  static void beforeAll() {
    CONTAINER.start();
    s3Client = S3Client.builder()
        .endpointOverride(URI.create(CONTAINER.getEndpoint()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(CONTAINER.getAccessKey(), CONTAINER.getSecretKey())
        ))
        .region(Region.US_EAST_1)
        .forcePathStyle(true)
        .build();
    s3Client.createBucket(builder -> builder.bucket(BUCKET));
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("S3_ENDPOINT", CONTAINER::getEndpoint);
    registry.add("S3_ACCESS_KEY", CONTAINER::getAccessKey);
    registry.add("S3_SECRET_KEY", CONTAINER::getSecretKey);
    registry.add("S3_BUCKET", () -> BUCKET);
  }

  @Override
  long partSize() {
    return zerowebS3Config.getPartSize();
  }

  @Override
  byte[] readFile(final Attachment attachment) {
    try {
      ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(builder -> builder
          .bucket(zerowebS3Config.getBucket())
          .key(attachment.objectKey())
      );
      return response.asByteArray();
    } catch (Exception _) {
      return new byte[0];
    }
  }

  @Override
  void assertIncorrectFileStatus(int status) {
    Assertions.assertEquals(ErrorCodeConstant.SERVER_ERROR_STATUS, status);
  }

  @Override
  void expire(final Attachment attachment) {
    s3UploadIdRepository.findById(attachment.getId())
        .ifPresent(s3UploadId -> s3Client.abortMultipartUpload(builder -> builder
            .bucket(BUCKET)
            .key(attachment.objectKey())
            .uploadId(s3UploadId.getUploadId())
        ));
  }

  @Override
  void saveFile(Attachment attachment) {
    s3Client.putObject(
        builder -> builder
            .bucket(BUCKET)
            .key(attachment.objectKey())
            .metadata(Collections.singletonMap("filename", attachment.getName()))
            .contentType(attachment.getType())
            .contentLength(attachment.getSize()),
        resource
    );
  }
}

@ActiveProfiles("fs")
class FsHttpTest extends AttachmentHttpTest {

  private static final Path TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir"))
      .resolve(BannerConstant.NAME);
  @Resource
  private ZerowebFsConfig zerowebFsConfig;

  @Override
  long partSize() {
    return zerowebFsConfig.getPartSize();
  }

  @Override
  byte[] readFile(final Attachment attachment) {
    Path path = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());
    if (!path.toFile().exists()) {
      return new byte[0];
    }
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new ReadFileException(e);
    }
  }

  @Override
  void assertIncorrectFileStatus(int status) {
    Assertions.assertEquals(ErrorCodeConstant.CLIENT_ERROR_STATUS, status);
  }

  @Override
  void expire(final Attachment attachment) {
    try (Stream<Path> stream = Files.walk(TEMP_DIR.resolve(attachment.getId()))) {
      stream.sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    } catch (IOException _) {
      // 无需处理
    }
  }

  @Override
  void saveFile(Attachment attachment) throws IOException {
    Path path = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());
    Files.createDirectories(path.getParent());
    Files.copy(resource, path);
  }
}
