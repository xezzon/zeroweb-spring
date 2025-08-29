package io.github.xezzon.zeroweb.attachment.internal;

import static io.github.xezzon.zeroweb.auth.AuthHttpConstant.AUTHORIZATION;
import static io.github.xezzon.zeroweb.auth.JwtFilter.PUBLIC_KEY_HEADER;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentReq;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentResp;
import io.github.xezzon.zeroweb.attachment.entity.UploadAddress;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.auth.TestJwtGenerator;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.core.util.ResourceUtil;
import io.github.xezzon.zeroweb.storage.file.FsService;
import jakarta.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.google.common.hash.Hashing;

/// @author xezzon
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class AttachmentHttpTest {

  private static final String ADD_ATTACHMENT = "/attachment";
  private static final String GET_UPLOAD_ADDRESS = "/attachment/{id}/endpoint/upload";
  private static final String FILE_NAME = "test.txt";

  private final Path resource = ResourceUtil.getResourceFromClasspath(FILE_NAME);
  private final Attachment attachment = new Attachment();

  @Value("${spring.servlet.multipart.max-file-size}")
  private String maxFileSize;
  @Resource
  private WebTestClient webTestClient;
  @Resource
  private AttachmentRepository repository;
  @Resource
  private ZerowebFileConfig zerowebFileConfig;

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
    attachment.setProvider(FileProviderEnum.FS);
    attachment.setStatus(AttachmentStatusEnum.UPLOADING);
    repository.save(attachment);
  }

  @Test
  void addAttachment() {
    final String userId = UUID.randomUUID().toString();

    AddAttachmentReq req = new AddAttachmentReq(
        RandomUtil.randomString(8),
        new String(RandomUtil.randomBytes(256 / 8)),
        RandomUtil.randomLong(),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        UUID.randomUUID().toString()
    );
    AddAttachmentResp responseBody = webTestClient.post()
        .uri(ADD_ATTACHMENT)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .header(AUTHORIZATION, TestJwtGenerator.userBuilder().id(userId).bearer())
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(AddAttachmentResp.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(maxFileSize, responseBody.maxPartSize() + "MB");
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
        new String(RandomUtil.randomBytes(256 / 8)),
        RandomUtil.randomLong(),
        RandomUtil.randomString(8),
        RandomUtil.randomString(8),
        UUID.randomUUID().toString()
    );
    AddAttachmentResp responseBody = webTestClient.post()
        .uri(ADD_ATTACHMENT)
        .header(PUBLIC_KEY_HEADER, TestJwtGenerator.getPublicKey())
        .bodyValue(req)
        .exchange()
        .expectStatus().isOk()
        .expectBody(AddAttachmentResp.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Attachment actual = repository.findById(responseBody.id()).orElseThrow();
    Assertions.assertNull(actual.getOwnerId());
  }

  @Test
  void getUploadAddress() {
    UploadAddress responseBody = webTestClient.get()
        .uri(GET_UPLOAD_ADDRESS, attachment.getId())
        .exchange()
        .expectStatus().isOk()
        .expectBody(UploadAddress.class)
        .returnResult().getResponseBody();
    Assertions.assertNotNull(responseBody);
    Assertions.assertEquals(attachment.getId(), responseBody.id());
    Assertions.assertEquals(attachment.getProvider(), responseBody.provider());
    Assertions.assertEquals(FsService.UPLOAD_ENDPOINT, responseBody.endpoint());
  }
}
