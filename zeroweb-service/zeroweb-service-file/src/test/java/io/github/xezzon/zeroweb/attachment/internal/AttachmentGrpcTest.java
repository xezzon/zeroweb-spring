package io.github.xezzon.zeroweb.attachment.internal;

import cn.hutool.core.util.RandomUtil;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentBlockingStub;
import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentStub;
import io.github.xezzon.zeroweb.attachment.AttachmentList;
import io.github.xezzon.zeroweb.attachment.FileDownloadRequest;
import io.github.xezzon.zeroweb.attachment.FileDownloadResponse;
import io.github.xezzon.zeroweb.attachment.FileMetadata;
import io.github.xezzon.zeroweb.attachment.FileUploadRequest;
import io.github.xezzon.zeroweb.attachment.FileUploadResponse;
import io.github.xezzon.zeroweb.attachment.QueryAttachmentListRequest;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFsConfig;
import io.github.xezzon.zeroweb.common.exception.WriteFileException;
import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@SpringBootTest
@DirtiesContext
@Slf4j
abstract class AttachmentGrpcTest {

  private static final String FILE_NAME = "test.txt";
  private static final String LARGE_FILE_NAME = "large_file.jpg";

  final Path resource = ResourceUtil.getResourceFromClasspath(FILE_NAME);
  final Path largeFileResource = ResourceUtil.getResourceFromClasspath(LARGE_FILE_NAME);
  private final Attachment attachment = new Attachment();
  private final Attachment largeFileAttachment = new Attachment();

  @Resource
  private AttachmentRepository repository;
  @Resource
  private AttachmentStub attachmentStub;
  @Resource
  private AttachmentBlockingStub blockingStub;

  abstract FileProviderEnum provider();

  abstract byte[] readFile(final Attachment attachment);

  abstract void saveFile(Attachment attachment, Path resource) throws IOException;

  static Stream<Path> files() {
    return Stream.of(
        ResourceUtil.getResourceFromClasspath(FILE_NAME),
        ResourceUtil.getResourceFromClasspath(LARGE_FILE_NAME)
    );
  }

  @BeforeEach
  void setUp() throws IOException {
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
    attachment.setProvider(this.provider());
    attachment.setStatus(AttachmentStatusEnum.UPLOADING);
    repository.save(attachment);
    this.saveFile(attachment, resource);
  }

  @BeforeEach
  void setUp_largeFile() throws IOException {
    File file = largeFileResource.toFile();
    largeFileAttachment.setName(LARGE_FILE_NAME);
    largeFileAttachment.setChecksum(Base64.getEncoder().encodeToString(
        Hashing.sha256()
            .hashBytes(Files.readAllBytes(largeFileResource))
            .asBytes()
    ));
    largeFileAttachment.setSize(file.length());
    largeFileAttachment.setType(Files.probeContentType(largeFileResource));
    largeFileAttachment.setBizType(RandomUtil.randomString(8));
    largeFileAttachment.setBizId(UUID.randomUUID().toString());
    largeFileAttachment.setProvider(this.provider());
    largeFileAttachment.setStatus(AttachmentStatusEnum.UPLOADING);
    repository.save(largeFileAttachment);
    this.saveFile(largeFileAttachment, largeFileResource);
  }

  @ParameterizedTest
  @MethodSource("files")
  void uploadFile(Path resource) throws IOException, InterruptedException {
    String fileName = resource.getFileName().toString();
    String fileType = Files.probeContentType(resource);
    long fileSize = resource.toFile().length();

    final CountDownLatch latch = new CountDownLatch(1);
    final FileUploadResponse[] response = new FileUploadResponse[1];

    StreamObserver<FileUploadRequest> requestObserver = attachmentStub
        .uploadFile(new StreamObserver<>() {
          @Override
          public void onNext(FileUploadResponse value) {
            response[0] = value;
          }

          @Override
          public void onError(Throwable t) {
            log.error("Upload failed.", t);
            latch.countDown();
          }

          @Override
          public void onCompleted() {
            latch.countDown();
          }
        });

    try (InputStream inputStream = Files.newInputStream(resource)) {
      requestObserver.onNext(FileUploadRequest.newBuilder()
          .setMetadata(FileMetadata.newBuilder()
              .setName(fileName)
              .setType(fileType)
              .setBizType(RandomUtil.randomString(8))
              .setBizId(UUID.randomUUID().toString())
              .build()
          )
          .setChunk(ByteString.readFrom(inputStream))
          .build()
      );
    }
    requestObserver.onCompleted();

    Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));
    Assertions.assertNotNull(response[0]);
    Assertions.assertNotNull(response[0].getId());
    Attachment savedAttachment = repository.findById(response[0].getId()).orElseThrow();
    Assertions.assertEquals(fileName, savedAttachment.getName());
    Assertions.assertEquals(fileType, savedAttachment.getType());
    Assertions.assertEquals(fileSize, savedAttachment.getSize());
    Assertions.assertArrayEquals(
        Files.readAllBytes(resource),
        this.readFile(savedAttachment)
    );
  }

  @Test
  void queryByBiz() {
    AttachmentList responseBody = blockingStub.queryAttachment(
        QueryAttachmentListRequest.newBuilder()
            .setBizType(attachment.getBizType())
            .setBizId(attachment.getBizId())
            .build()
    );

    Assertions.assertEquals(1, responseBody.getItemsCount());
    Assertions.assertEquals(attachment.getId(), responseBody.getItems(0).getId());
  }

  @Test
  void queryByBiz_empty() {
    AttachmentList responseBody = blockingStub.queryAttachment(
        QueryAttachmentListRequest.newBuilder()
            .setBizType(RandomUtil.randomString(6))
            .setBizId(attachment.getBizId())
            .build()
    );
    Assertions.assertEquals(0, responseBody.getItemsCount());

    AttachmentList responseBody2 = blockingStub.queryAttachment(
        QueryAttachmentListRequest.newBuilder()
            .setBizType(attachment.getBizType())
            .setBizId(UUID.randomUUID().toString())
            .build()
    );
    Assertions.assertEquals(0, responseBody2.getItemsCount());
  }

  @Test
  void download() throws IOException, InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final FileDownloadResponse[] response = new FileDownloadResponse[1];

    attachmentStub.downloadFile(
        FileDownloadRequest.newBuilder()
            .setId(attachment.getId())
            .build(),
        new StreamObserver<>() {
          @Override
          public void onNext(FileDownloadResponse value) {
            response[0] = value;
          }

          @Override
          public void onError(Throwable t) {
            log.error("Download failed.", t);
            latch.countDown();
          }

          @Override
          public void onCompleted() {
            latch.countDown();
          }
        }
    );

    Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));
    Assertions.assertNotNull(response[0]);
    Assertions.assertEquals(attachment.getId(), response[0].getMetadata().getId());
    Assertions.assertArrayEquals(
        Files.readAllBytes(resource),
        response[0].getChunk().toByteArray()
    );
  }

  @Test
  void download_largeFile() throws IOException, InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final FileDownloadResponse[] response = new FileDownloadResponse[1];

    attachmentStub.downloadFile(FileDownloadRequest.newBuilder()
            .setId(largeFileAttachment.getId())
            .build(),
        new StreamObserver<>() {
          @Override
          public void onNext(FileDownloadResponse value) {
            response[0] = value;
          }

          @Override
          public void onError(Throwable t) {
            log.error("Download failed.", t);
            latch.countDown();
          }

          @Override
          public void onCompleted() {
            latch.countDown();
          }
        });

    Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));
    Assertions.assertNotNull(response[0]);
    Assertions.assertEquals(largeFileAttachment.getId(), response[0].getMetadata().getId());
    Assertions.assertArrayEquals(
        Files.readAllBytes(largeFileResource),
        response[0].getChunk().toByteArray()
    );
  }
}

@ActiveProfiles("s3")
@Slf4j
class S3GrpcTest extends AttachmentGrpcTest {

  private static final LocalStackContainer CONTAINER = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:s3-latest")
  ).withServices(Service.S3);
  private static final String BUCKET = "test";
  private static S3Client s3Client = null;

  @BeforeAll
  static void beforeAll() {
    CONTAINER.start();
    s3Client = S3Client.builder()
        .endpointOverride(CONTAINER.getEndpointOverride(Service.S3))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(CONTAINER.getAccessKey(), CONTAINER.getSecretKey())
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
    registry.add("S3_ENDPOINT", () -> CONTAINER.getEndpointOverride(Service.S3));
    registry.add("S3_ACCESS_KEY", CONTAINER::getAccessKey);
    registry.add("S3_SECRET_KEY", CONTAINER::getSecretKey);
    registry.add("S3_BUCKET", () -> BUCKET);
  }

  @Override
  FileProviderEnum provider() {
    return FileProviderEnum.S3;
  }

  @Override
  protected byte[] readFile(final Attachment attachment) {
    try {
      ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(builder -> builder
          .bucket(BUCKET)
          .key(attachment.objectKey())
      );
      return response.asByteArray();
    } catch (Exception e) {
      log.error("Unable to read file from s3.", e);
      return new byte[0];
    }
  }

  @Override
  void saveFile(final Attachment attachment, final Path resource) {
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
@Slf4j
class FsGrpcTest extends AttachmentGrpcTest {

  @Resource
  private ZerowebFsConfig zerowebFsConfig;

  @Override
  FileProviderEnum provider() {
    return FileProviderEnum.FS;
  }

  @Override
  protected byte[] readFile(final Attachment attachment) {
    Path path = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());
    if (!path.toFile().exists()) {
      log.error("File not exist.");
      return new byte[0];
    }
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new WriteFileException(e);
    }
  }

  @Override
  void saveFile(final Attachment attachment, final Path resource) throws IOException {
    Path path = zerowebFsConfig.getBasePath().resolve(attachment.objectKey());
    Files.createDirectories(path.getParent());
    Files.copy(resource, path);
  }
}
