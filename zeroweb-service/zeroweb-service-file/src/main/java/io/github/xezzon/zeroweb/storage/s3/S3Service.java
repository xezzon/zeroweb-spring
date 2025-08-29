package io.github.xezzon.zeroweb.storage.s3;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.UploadAddress;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.storage.IStorageService;
import java.time.Duration;
import java.util.Collections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/// @author xezzon
@Service
@ConditionalOnBean(ZerowebS3Config.class)
public class S3Service implements IStorageService {

  private final ZerowebS3Config zerowebS3Config;
  private final S3Presigner s3Presigner;

  public S3Service(
      final ZerowebS3Config zerowebS3Config,
      final S3Presigner s3Presigner
  ) {
    this.zerowebS3Config = zerowebS3Config;
    this.s3Presigner = s3Presigner;
  }

  @Override
  public FileProviderEnum provider() {
    return FileProviderEnum.S3;
  }

  @Override
  public UploadAddress getUploadAddress(Attachment attachment) {
    PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .putObjectRequest(builder -> builder
            .bucket(zerowebS3Config.getBucket())
            .key(attachment.objectKey())
            .contentType(attachment.getType())
            .contentLength(attachment.getSize())
            .checksumAlgorithm(ChecksumAlgorithm.SHA256)
            .checksumSHA256(attachment.getChecksum())
            .metadata(Collections.singletonMap("filename", attachment.getName()))
        )
        .build();
    PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner
        .presignPutObject(putObjectPresignRequest);
    return new UploadAddress(
        attachment.getId(),
        attachment.getProvider(),
        presignedPutObjectRequest.url().toString()
    );
  }
}

