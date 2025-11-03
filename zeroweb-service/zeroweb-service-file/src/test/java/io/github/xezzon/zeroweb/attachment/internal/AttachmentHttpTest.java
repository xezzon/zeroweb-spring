package io.github.xezzon.zeroweb.attachment.internal;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.hutool.core.util.RandomUtil;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentReq;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo.Address;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.common.config.ZerowebFsConfig;
import io.github.xezzon.zeroweb.common.exception.ErrorCodeConstant;
import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import io.github.xezzon.zeroweb.storage.s3.entity.S3Etag;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.testcontainers.containers.MinIOContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
abstract class AttachmentHttpTest {

  private static final String ADD_ATTACHMENT = "/attachment";
  private static final String GET_UPLOAD_ADDRESS = "/attachment/{id}/endpoint/upload";
  private static final String FINISH_UPLOAD = "/attachment/{id}/status/done";
  private static final String QUERY_BY_BIZ = "/attachment/list";
  private static final String FILE_NAME = "test.txt";
  private static final String LARGE_FILE = "large_file.jpg";

  private final Path resource = ResourceUtil.getResourceFromClasspath(FILE_NAME);
  private final Path largeFileResource = ResourceUtil.getResourceFromClasspath(LARGE_FILE);
  private final Attachment attachment = new Attachment();
  private Attachment largeFileAttachment = new Attachment();
  private final List<byte[]> largeFileParts = new ArrayList<>();

  @Resource
  private WebTestClient webTestClient;
  @Resource
  private AttachmentRepository repository;
  @Resource
  private ZerowebFileConfig zerowebFileConfig;
  @LocalServerPort
  private int port;

  abstract boolean fileExist(Attachment attachment);

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
      long length = Math.min(zerowebFileConfig.getMaxPartSize(), byteSource.size() - offset);
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

  @Test
  void addAttachment() {
    final String userId = UUID.randomUUID().toString();

    AddAttachmentReq req = new AddAttachmentReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(44),
        RandomUtil.randomLong(5) * 1024 * 1024,
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        UUID.randomUUID().toString()
    );
    UploadInfo responseBody = webTestClient.post()
        .uri(ADD_ATTACHMENT)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(userId).bearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(
        (req.size() - 1) / (zerowebFileConfig.getMaxPartSize()) + 1,
        responseBody.partCount()
    );
    Assertions.assertEquals(responseBody.partCount(), responseBody.addresses().size());
    Attachment actual = repository.findById(responseBody.id()).orElseThrow();
    Assertions.assertEquals(zerowebFileConfig.getProvider(), actual.getProvider());
    Assertions.assertEquals(AttachmentStatusEnum.UPLOADING, actual.getStatus());
    Assertions.assertEquals(userId, actual.getOwnerId());
    Assertions.assertTrue(Duration.between(actual.getCreateTime(), Instant.now()).toMinutes() < 1);
    Assertions.assertTrue(Duration.between(actual.getCreateTime(), Instant.now()).toMillis() > 0);
  }

  @Test
  void addAttachment_notLogin() {
    AddAttachmentReq req = new AddAttachmentReq(
        RandomUtil.randomString(8),
        RandomUtil.randomString(44),
        RandomUtil.randomLong(5) * 1024 * 1024,
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        UUID.randomUUID().toString()
    );
    UploadInfo responseBody = webTestClient.post()
        .uri(ADD_ATTACHMENT)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .bodyValue(req)
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
    UploadInfo responseBody = webTestClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_ADDRESS)
            .queryParam("checksum", attachment.getChecksum())
            .queryParam("fileSize", attachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);

    URI uri = localhost().resolve(responseBody.addresses().getFirst().getEndpoint());
    webTestClient.put()
        .uri(uri)
        .bodyValue(Files.readAllBytes(resource))
        .header("Content-Type", Files.probeContentType(resource))
        .header("x-amz-meta-filename", attachment.getName())
        .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
        .header("x-amz-checksum-sha256", attachment.getChecksum())
        .exchange()
        .expectStatus().isOk();

    Assertions.assertTrue(fileExist(attachment));
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
    UploadInfo responseBody = webTestClient.post()
        .uri(builder -> builder
            .path(ADD_ATTACHMENT)
            .queryParam("crc", crc32.getValue())
            .build()
        )
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(responseBody.partCount() > 1);
    final String attachmentId = responseBody.id();
    largeFileAttachment = repository.findById(attachmentId).orElseThrow();

    // 上传其中一个分片
    {
      Address address = responseBody.addresses().get(1);
      byte[] partContent = largeFileParts.get(1);
      String checksum = Base64.getEncoder().encodeToString(
          Hashing.sha256().hashBytes(partContent).asBytes()
      );
      URI uri = localhost().resolve(URI.create(address.getEndpoint()));
      ResponseSpec response = webTestClient.put()
          .uri(uri)
          .bodyValue(partContent)
          .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
          .header("x-amz-checksum-sha256", checksum)
          .exchange()
          .expectStatus().isOk();
      if (address.getCallback() != null) {
        response.expectHeader().value("etag", etag -> webTestClient.put()
            .uri(builder -> builder
                .path(address.getCallback())
                .build(attachmentId)
            )
            .bodyValue(new S3Etag(attachmentId, 2, etag, checksum))
            .exchange()
            .expectStatus().isOk()
        );
      }
      // 重复上传，测试幂等性
      webTestClient.put()
          .uri(uri)
          .bodyValue(partContent)
          .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
          .header("x-amz-checksum-sha256", checksum)
          .exchange()
          .expectStatus().isOk();
    }

    // 测试断点续传
    {
      responseBody = webTestClient.get()
          .uri(builder -> builder
              .path(GET_UPLOAD_ADDRESS)
              .queryParam("checksum", largeFileAttachment.getChecksum())
              .queryParam("fileSize", largeFileAttachment.getSize())
              .build(attachmentId)
          )
          .exchange()
          .expectStatus().isOk()
          .expectBody(UploadInfo.class)
          .returnResult().getResponseBody();
      Assertions.assertNotNull(responseBody);
      Assertions.assertTrue(responseBody.addresses().size() < responseBody.partCount());

      responseBody.addresses().parallelStream()
          .forEach(address -> {
            final byte[] partContent = largeFileParts.get(address.getPartNumber() - 1);
            final URI uri = localhost().resolve(URI.create(address.getEndpoint()));
            final String checksum = Base64.getEncoder().encodeToString(
                Hashing.sha256().hashBytes(partContent).asBytes()
            );
            ResponseSpec response = webTestClient.put()
                .uri(uri)
                .bodyValue(partContent)
                .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
                .header("x-amz-checksum-sha256", checksum)
                .exchange()
                .expectStatus().isOk();
            if (address.getCallback() != null) {
              response.expectHeader().value("etag", etag -> webTestClient.put()
                  .uri(builder -> builder
                      .path(address.getCallback())
                      .build(attachmentId)
                  )
                  .bodyValue(new S3Etag(attachmentId, address.getPartNumber(), etag, checksum))
                  .exchange()
                  .expectStatus().isOk()
              );
            }
          });
    }

    webTestClient.put()
        .uri(FINISH_UPLOAD, attachmentId)
        .exchange()
        .expectStatus().isOk();
    Assertions.assertTrue(fileExist(largeFileAttachment));
  }

  @Test
  void upload_incorrectChecksum() {
    webTestClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_ADDRESS)
            .queryParam("checksum", largeFileAttachment.getChecksum())
            .queryParam("fileSize", attachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS);

    webTestClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_ADDRESS)
            .queryParam("checksum", attachment.getChecksum())
            .queryParam("fileSize", largeFileAttachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS);

    webTestClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_ADDRESS)
            .queryParam("checksum", largeFileAttachment.getChecksum())
            .queryParam("fileSize", largeFileAttachment.getSize())
            .build(attachment.getId())
        )
        .exchange()
        .expectStatus().isEqualTo(ErrorCodeConstant.CLIENT_ERROR_STATUS);
  }

  @Test
  void upload_incorrectFile() throws IOException {
    UploadInfo responseBody = webTestClient.get()
        .uri(builder -> builder
            .path(GET_UPLOAD_ADDRESS)
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
    URI uri = localhost().resolve(responseBody.addresses().getFirst().getEndpoint());
    webTestClient.put()
        .uri(uri)
        .bodyValue(Files.readAllBytes(path))
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
      long length = Math.min(zerowebFileConfig.getMaxPartSize(), byteSource.size() - offset);
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
    UploadInfo responseBody = webTestClient.post()
        .uri(builder -> builder
            .path(ADD_ATTACHMENT)
            .queryParam("crc", crc32.getValue())
            .build()
        )
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(responseBody.partCount() > 1);
    final String attachmentId = responseBody.id();
    largeFileAttachment = repository.findById(attachmentId).orElseThrow();

    responseBody.addresses().parallelStream()
        .forEach(address -> {
          if (address.getPartNumber() > incorrectParts.size()) {
            return;
          }
          final byte[] partContent = incorrectParts.get(address.getPartNumber() - 1);
          final URI uri = localhost().resolve(URI.create(address.getEndpoint()));
          final String checksum = Base64.getEncoder().encodeToString(
              Hashing.sha256().hashBytes(partContent).asBytes()
          );
          ResponseSpec response = webTestClient.put()
              .uri(uri)
              .bodyValue(partContent)
              .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
              .header("x-amz-checksum-sha256", checksum)
              .exchange()
              .expectStatus().isOk();
          if (address.getCallback() != null) {
            response.expectHeader().value("etag", etag -> webTestClient.put()
                .uri(builder -> builder
                    .path(address.getCallback())
                    .build(attachmentId)
                )
                .bodyValue(new S3Etag(attachmentId, address.getPartNumber(), etag, checksum))
                .exchange()
                .expectStatus().isOk()
            );
          }
        });

    webTestClient.put()
        .uri(FINISH_UPLOAD, attachmentId)
        .exchange()
        .expectStatus().is4xxClientError();
  }

  @Test
  void upload_incorrectPartSize() throws IOException {
    ByteSource byteSource = ByteSource.wrap(Files.readAllBytes(largeFileResource));
    long offset = 0;
    int incorrectPartSize = zerowebFileConfig.getMaxPartSize() + 1024 * 1024;
    List<byte[]> incorrectParts = new ArrayList<>();
    while (offset < byteSource.size()) {
      long length = Math.min(incorrectPartSize, byteSource.size() - offset);
      incorrectParts.add(byteSource.slice(offset, length).read());
      offset += length;
    }
    CRC32 crc32 = new CRC32();
    crc32.update(byteSource.read());
    // 新增附件
    AddAttachmentReq req = new AddAttachmentReq(
        largeFileAttachment.getName(),
        largeFileAttachment.getChecksum(),
        largeFileAttachment.getSize(),
        largeFileAttachment.getType(),
        largeFileAttachment.getBizType(),
        largeFileAttachment.getBizId()
    );
    UploadInfo responseBody = webTestClient.post()
        .uri(builder -> builder
            .path(ADD_ATTACHMENT)
            .queryParam("crc", crc32.getValue())
            .build()
        )
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadInfo.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(responseBody.partCount() > 1);
    final String attachmentId = responseBody.id();
    largeFileAttachment = repository.findById(attachmentId).orElseThrow();

    responseBody.addresses().parallelStream()
        .forEach(address -> {
          final byte[] partContent = incorrectParts.get(address.getPartNumber() - 1);
          final URI uri = localhost().resolve(URI.create(address.getEndpoint()));
          final String checksum = Base64.getEncoder().encodeToString(
              Hashing.sha256().hashBytes(partContent).asBytes()
          );
          ResponseSpec response = webTestClient.put()
              .uri(uri)
              .bodyValue(partContent)
              .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
              .header("x-amz-checksum-sha256", checksum)
              .exchange()
              .expectStatus().isOk();
          if (address.getCallback() != null) {
            response.expectHeader().value("etag", etag -> webTestClient.put()
                .uri(builder -> builder
                    .path(address.getCallback())
                    .build(attachmentId)
                )
                .bodyValue(new S3Etag(attachmentId, address.getPartNumber(), etag, checksum))
                .exchange()
                .expectStatus().isOk()
            );
          }
        });

    webTestClient.put()
        .uri(FINISH_UPLOAD, attachmentId)
        .exchange()
        .expectStatus().isOk();
    Assertions.assertTrue(fileExist(largeFileAttachment));
  }

  @Test
  void finishUpload() {
    Attachment before = repository.findById(attachment.getId()).orElseThrow();
    Assertions.assertEquals(AttachmentStatusEnum.UPLOADING, before.getStatus());

    webTestClient.put()
        .uri(FINISH_UPLOAD, attachment.getId())
        .exchange()
        .expectStatus().isOk();

    Attachment after = repository.findById(attachment.getId()).orElseThrow();
    Assertions.assertEquals(AttachmentStatusEnum.DONE, after.getStatus());
  }

  @Test
  void queryByBiz() {
    List<Attachment> responseBody = webTestClient.get()
        .uri(uri -> uri.path(QUERY_BY_BIZ)
            .queryParam("bizType", attachment.getBizType())
            .queryParam("bizId", attachment.getBizId())
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Attachment.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);

    Assertions.assertEquals(1, responseBody.size());
    Assertions.assertEquals(attachment.getId(), responseBody.getFirst().getId());
  }

  @Test
  void queryByBiz_empty() {
    List<Attachment> responseBody = webTestClient.get()
        .uri(uri -> uri.path(QUERY_BY_BIZ)
            .queryParam("bizType", RandomUtil.randomString(6))
            .queryParam("bizId", attachment.getBizId())
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Attachment.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertTrue(responseBody.isEmpty());

    List<Attachment> responseBody2 = webTestClient.get()
        .uri(uri -> uri.path(QUERY_BY_BIZ)
            .queryParam("bizType", attachment.getBizType())
            .queryParam("bizId", UUID.randomUUID().toString())
            .build()
        )
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Attachment.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody2);
    Assertions.assertTrue(responseBody2.isEmpty());
  }

  private URI localhost() {
    return URI.create("http://localhost:" + port);
  }
}

@ActiveProfiles("s3")
class S3HttpTest extends AttachmentHttpTest {

  private static final MinIOContainer CONTAINER = new MinIOContainer("minio/minio:latest");
  private static final String BUCKET = "test";
  private static S3Client s3Client = null;

  @BeforeAll
  static void beforeAll() {
    CONTAINER.start();
    s3Client = S3Client.builder()
        .endpointOverride(URI.create(CONTAINER.getS3URL()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(CONTAINER.getUserName(), CONTAINER.getPassword())
        ))
        .region(Region.US_EAST_1)
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()
        )
        .build();
    s3Client.createBucket(builder -> builder.bucket(BUCKET));
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("S3_ENDPOINT", CONTAINER::getS3URL);
    registry.add("S3_ACCESS_KEY", CONTAINER::getUserName);
    registry.add("S3_SECRET_KEY", CONTAINER::getPassword);
    registry.add("S3_BUCKET", () -> BUCKET);
  }

  @Override
  boolean fileExist(Attachment attachment) {
    s3Client.headObject(builder -> builder
        .bucket(BUCKET)
        .key(attachment.objectKey())
    );
    return true;
  }
}

@ActiveProfiles("fs")
class FsHttpTest extends AttachmentHttpTest {

  @Resource
  private ZerowebFsConfig zerowebFsConfig;

  @Override
  boolean fileExist(Attachment attachment) {
    Path path = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());
    return Files.exists(path);
  }
}
