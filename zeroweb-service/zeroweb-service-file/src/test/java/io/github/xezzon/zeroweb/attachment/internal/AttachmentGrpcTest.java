package io.github.xezzon.zeroweb.attachment.internal;

import cn.hutool.core.util.RandomUtil;
import com.google.protobuf.ByteString;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentStub;
import io.github.xezzon.zeroweb.attachment.FileMetadata;
import io.github.xezzon.zeroweb.attachment.FileUploadRequest;
import io.github.xezzon.zeroweb.attachment.FileUploadResponse;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@SpringBootTest
@DirtiesContext
@Slf4j
abstract class AttachmentGrpcTest {

  @Resource
  private AttachmentRepository attachmentRepository;
  @Resource
  private AttachmentStub attachmentStub;

  static Stream<Path> files() {
    return Stream.of(
        ResourceUtil.getResourceFromClasspath("test.txt"),
        ResourceUtil.getResourceFromClasspath("large_file.jpg")
    );
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
    Attachment savedAttachment = attachmentRepository.findById(response[0].getId()).orElseThrow();
    Assertions.assertEquals(fileName, savedAttachment.getName());
    Assertions.assertEquals(fileType, savedAttachment.getType());
    Assertions.assertEquals(fileSize, savedAttachment.getSize());
  }
}

@ActiveProfiles("s3")
class S3GrpcTest extends AttachmentGrpcTest {

  private static final LocalStackContainer CONTAINER = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:s3-latest")
  ).withServices(Service.S3);
  private static final String BUCKET = "test";

  @BeforeAll
  static void beforeAll() {
    CONTAINER.start();
    try (
        S3Client s3Client = S3Client.builder()
            .endpointOverride(CONTAINER.getEndpointOverride(Service.S3))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(CONTAINER.getAccessKey(), CONTAINER.getSecretKey())
            ))
            .region(Region.US_EAST_1)
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build()
            )
            .build()
    ) {
      s3Client.createBucket(builder -> builder.bucket(BUCKET));
    }
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("S3_ENDPOINT", () -> CONTAINER.getEndpointOverride(Service.S3));
    registry.add("S3_ACCESS_KEY", CONTAINER::getAccessKey);
    registry.add("S3_SECRET_KEY", CONTAINER::getSecretKey);
    registry.add("S3_BUCKET", () -> BUCKET);
  }
}

@ActiveProfiles("fs")
class FsGrpcTest extends AttachmentGrpcTest {

}
