package io.github.xezzon.zeroweb.storage.s3;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.UploadAddress;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import jakarta.annotation.Resource;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.shaded.com.google.common.hash.Hashing;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

/// @author xezzon
@SpringBootTest
@DirtiesContext
class S3ServiceTest {

  private static final MinIOContainer CONTAINER = new MinIOContainer("minio/minio:latest");
  private static final String BUCKET = "test";
  private static final String FILE_NAME = "test.txt";
  private static S3Client s3Client = null;
  private final Path resource = ResourceUtil.getResourceFromClasspath(FILE_NAME);
  private final Attachment attachment = new Attachment();
  @Resource
  private S3Service s3Service;
  @Resource
  private AttachmentRepository attachmentRepository;

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
    registry.add("ZEROWEB_FILE_PROVIDER", () -> "s3");
    registry.add("S3_ENDPOINT", CONTAINER::getS3URL);
    registry.add("S3_ACCESS_KEY", CONTAINER::getUserName);
    registry.add("S3_SECRET_KEY", CONTAINER::getPassword);
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
    UploadAddress uploadAddress = s3Service.getUploadAddress(attachment);

    try (HttpClient httpClient = HttpClient.newHttpClient()) {
      final HttpResponse<byte[]> response = httpClient.send(
          HttpRequest.newBuilder()
              .uri(URI.create(uploadAddress.endpoint()))
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
}
