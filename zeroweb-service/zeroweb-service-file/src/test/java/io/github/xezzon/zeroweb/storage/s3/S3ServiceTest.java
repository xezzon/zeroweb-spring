package io.github.xezzon.zeroweb.storage.s3;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import io.github.xezzon.zeroweb.storage.StorageContext;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
import io.github.xezzon.zeroweb.storage.s3.entity.S3Etag;
import io.github.xezzon.zeroweb.storage.s3.entity.S3UploadId;
import io.github.xezzon.zeroweb.storage.s3.repository.S3UploadIdRepository;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.CRC32;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.shaded.com.google.common.hash.Hashing;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;

/// @author xezzon
@SpringBootTest
@DirtiesContext
class S3ServiceTest {

  private static final LocalStackContainer CONTAINER = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:s3-latest")
  );
  private static final String BUCKET = "test";
  private static final String FILE_NAME = "test.txt";
  private static final String LARGE_FILE_NAME = "large_file.jpg";
  private static S3Client s3Client = null;
  private final Path resource = ResourceUtil.getResourceFromClasspath(FILE_NAME);
  private final Attachment attachment = new Attachment();
  @Resource
  private S3Service s3Service;
  @Resource
  private AttachmentRepository attachmentRepository;
  @Resource
  private S3UploadIdRepository s3UploadIdRepository;
  @Resource
  private ZerowebS3Config zerowebS3Config;

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
    registry.add("ZEROWEB_FILE_PROVIDER", () -> "s3");
    registry.add("S3_ENDPOINT", () -> CONTAINER.getEndpointOverride(Service.S3));
    registry.add("S3_ACCESS_KEY", CONTAINER::getAccessKey);
    registry.add("S3_SECRET_KEY", CONTAINER::getSecretKey);
    registry.add("S3_BUCKET", () -> BUCKET);
  }

  @BeforeEach
  void setUp() throws Exception {
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
    attachment.setProvider(FileProviderEnum.S3);
    attachment.setStatus(AttachmentStatusEnum.UPLOADING);
    attachmentRepository.save(attachment);
  }

  @Test
  void getUploadAddress() throws Exception {
    UploadEndpoint uploadInfo = s3Service.getUploadAddress(attachment);

    try (HttpClient httpClient = HttpClient.newHttpClient()) {
      final HttpResponse<byte[]> response = httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(uploadInfo.getEndpoint()))
              .header("Content-Type", Files.probeContentType(resource))
              .header("x-amz-meta-filename", resource.toFile().getName())
              .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.SHA256.toString())
              .header("x-amz-checksum-sha256", attachment.getChecksum())
              .PUT(HttpRequest.BodyPublishers.ofFile(resource))
              .build(),
          HttpResponse.BodyHandlers.ofByteArray()
      );

      Assertions.assertEquals(200, response.statusCode());
    }
    HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
        .bucket(BUCKET)
        .key(attachment.objectKey())
        .build();
    HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
    Assertions.assertEquals(attachment.getName(), headObjectResponse.metadata().get("filename"));
  }

  @Test
  void getUploadAddress_largeFile() throws Exception {
    final Path largeFileResource = ResourceUtil.getResourceFromClasspath(LARGE_FILE_NAME);
    File file = largeFileResource.toFile();
    String resourceType = Files.probeContentType(largeFileResource);
    byte[] resourceContent = Files.readAllBytes(largeFileResource);
    Attachment largeFileAttachment = new Attachment();
    largeFileAttachment.setName(FILE_NAME);
    largeFileAttachment.setChecksum(Base64.getEncoder().encodeToString(
        Hashing.sha256()
            .hashBytes(resourceContent)
            .asBytes()
    ));
    largeFileAttachment.setSize(file.length());
    largeFileAttachment.setType(resourceType);
    largeFileAttachment.setBizType(RandomUtil.randomString(8));
    largeFileAttachment.setBizId(UUID.randomUUID().toString());
    largeFileAttachment.setProvider(FileProviderEnum.S3);
    largeFileAttachment.setStatus(AttachmentStatusEnum.UPLOADING);
    attachmentRepository.save(largeFileAttachment);

    CRC32 crc32 = new CRC32();
    crc32.update(resourceContent);
    String checksum = Base64.getEncoder().encodeToString(
        HexUtil.decodeHex(Long.toHexString(crc32.getValue()))
    );
    int partSize = zerowebS3Config.getPartSize();
    int partCount = Math.toIntExact((file.length() - 1) / partSize) + 1;
    ScopedValue.where(StorageContext.CRC, checksum)
        .run(() -> {
          try (HttpClient httpClient = HttpClient.newHttpClient()) {
            int fromIndex = 0;
            for (int partNumber = 1; partNumber <= partCount; partNumber++) {
              int toIndex = fromIndex + partSize;
              byte[] partContent = Arrays.copyOfRange(resourceContent, fromIndex, toIndex);
              final CRC32 partCrc32 = new CRC32();
              partCrc32.update(partContent);
              String partChecksum = Base64.getEncoder().encodeToString(
                  HexUtil.decodeHex(Long.toHexString(partCrc32.getValue()))
              );
              fromIndex = toIndex;
              UploadEndpoint uploadInfo = s3Service
                  .getUploadAddress(largeFileAttachment, partNumber, partChecksum);

              final HttpResponse<byte[]> response = httpClient.send(
                  HttpRequest.newBuilder()
                      .uri(URI.create(uploadInfo.getEndpoint()))
                      .PUT(HttpRequest.BodyPublishers.ofByteArray(partContent))
                      // 兼容 MinIO、LocalStack
                      .header("x-amz-sdk-checksum-algorithm", ChecksumAlgorithm.CRC32.toString())
                      .header("x-amz-checksum-crc32", partChecksum)
                      .build(),
                  HttpResponse.BodyHandlers.ofByteArray()
              );

              Assertions.assertEquals(
                  200,
                  response.statusCode(),
                  () -> new String(response.body())
              );
              s3Service.upsertEtag(new S3Etag(
                  largeFileAttachment.getId(),
                  partNumber,
                  String.join(",", response.headers().allValues("etag")),
                  partChecksum
              ));
            }
          } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
          }
        });

    Optional<S3UploadId> s3UploadId = s3UploadIdRepository.findById(largeFileAttachment.getId());
    Assertions.assertTrue(s3UploadId.isPresent());
    ListPartsResponse listPartsResponse = s3Client.listParts(builder -> builder
        .bucket(BUCKET)
        .key(largeFileAttachment.objectKey())
        .uploadId(s3UploadId.get().getUploadId())
    );
    Assertions.assertEquals(partCount, listPartsResponse.parts().size());
  }
}
