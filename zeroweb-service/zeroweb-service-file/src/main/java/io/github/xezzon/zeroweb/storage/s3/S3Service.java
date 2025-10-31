package io.github.xezzon.zeroweb.storage.s3;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo.Address;
import io.github.xezzon.zeroweb.attachment.event.AttachmentCreatedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentUploadedEvent;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.storage.IStorageService;
import io.github.xezzon.zeroweb.storage.s3.entity.S3Etag;
import io.github.xezzon.zeroweb.storage.s3.entity.S3UploadId;
import io.github.xezzon.zeroweb.storage.s3.repository.S3EtagRepository;
import io.github.xezzon.zeroweb.storage.s3.repository.S3UploadIdRepository;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.ChecksumType;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/// @author xezzon
@Service
@ConditionalOnBean(ZerowebS3Config.class)
public class S3Service implements IStorageService {

  public static final String ETAG_CALLBACK_URL = "/s3/{id}/etag";
  private final ZerowebFileConfig zerowebFileConfig;
  private final ZerowebS3Config zerowebS3Config;
  private final S3Presigner s3Presigner;
  private final S3Client s3Client;
  private final S3UploadIdRepository s3UploadIdRepository;
  private final S3EtagRepository s3EtagRepository;

  public S3Service(
      final ZerowebS3Config zerowebS3Config,
      final S3Presigner s3Presigner,
      final S3Client s3Client,
      final S3UploadIdRepository s3UploadIdRepository,
      final S3EtagRepository s3EtagRepository,
      final ZerowebFileConfig zerowebFileConfig
  ) {
    this.zerowebS3Config = zerowebS3Config;
    this.s3Presigner = s3Presigner;
    this.s3Client = s3Client;
    this.s3UploadIdRepository = s3UploadIdRepository;
    this.s3EtagRepository = s3EtagRepository;
    this.zerowebFileConfig = zerowebFileConfig;
  }

  @Override
  public FileProviderEnum provider() {
    return FileProviderEnum.S3;
  }

  @Override
  public Address getUploadAddress(Attachment attachment) {
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
    return new Address(presignedPutObjectRequest.url().toString());
  }

  @Override
  public Address getUploadAddress(Attachment attachment, int partNumber) {
    // 如果分段已上传，则跳过
    Optional<S3Etag> etag = s3EtagRepository
        .findByAttachmentIdAndPartNumber(attachment.getId(), partNumber);
    if (etag.isPresent()) {
      return null;
    }

    S3UploadId s3UploadId = s3UploadIdRepository.findById(attachment.getId()).orElseThrow();
    PresignedUploadPartRequest presignedUploadPartRequest = s3Presigner
        .presignUploadPart(presignRequest -> presignRequest
            .signatureDuration(Duration.ofMinutes(10))
            .uploadPartRequest(partRequest -> partRequest
                .bucket(zerowebS3Config.getBucket())
                .key(attachment.objectKey())
                .uploadId(s3UploadId.getUploadId())
                .partNumber(partNumber)
            )
        );
    String callbackUrl = UriComponentsBuilder
        .fromPath(ETAG_CALLBACK_URL)
        .buildAndExpand(attachment.getId())
        .toUriString();
    return new Address(
        partNumber,
        presignedUploadPartRequest.url().toString(),
        callbackUrl
    );
  }

  void upsertEtag(S3Etag etag) {
    s3EtagRepository.findByAttachmentIdAndPartNumber(etag.getAttachmentId(), etag.getPartNumber())
        .ifPresentOrElse(
            entity -> {
              entity.setEtag(etag.getEtag());
              entity.setChecksum(etag.getChecksum());
              s3EtagRepository.save(entity);
            },
            () -> s3EtagRepository.save(etag)
        );
  }

  private String createMultipartUpload(Attachment attachment) {
    CreateMultipartUploadResponse response = s3Client.createMultipartUpload(builder -> builder
        .bucket(zerowebS3Config.getBucket())
        .key(attachment.objectKey())
        .contentType(attachment.getType())
        .checksumAlgorithm(ChecksumAlgorithm.SHA256)
        .checksumType(ChecksumType.COMPOSITE)
        .metadata(Collections.singletonMap("filename", attachment.getName()))
    );
    S3UploadId s3UploadId = new S3UploadId(attachment.getId(), response.uploadId());
    s3UploadIdRepository.save(s3UploadId);
    return response.uploadId();
  }

  @EventListener
  void listen(AttachmentCreatedEvent event) {
    Attachment attachment = event.attachment();
    if (attachment.getProvider() != FileProviderEnum.S3) {
      return;
    }
    if (attachment.getSize() <= zerowebFileConfig.getMaxPartSize()) {
      return;
    }
    this.createMultipartUpload(attachment);
  }

  @EventListener
  void listen(AttachmentUploadedEvent event) {
    Attachment attachment = event.attachment();
    if (attachment.getProvider() != FileProviderEnum.S3) {
      return;
    }
    s3UploadIdRepository.findById(attachment.getId())
        .ifPresent(s3UploadId -> {
          List<S3Etag> s3EtagList = s3EtagRepository
              .findByAttachmentIdOrderByPartNumberAsc(attachment.getId());
          List<CompletedPart> uploadedParts = s3EtagList.stream()
              .map(s3Etag -> CompletedPart.builder()
                  .partNumber(s3Etag.getPartNumber())
                  .eTag(s3Etag.getEtag())
                  .checksumSHA256(s3Etag.getChecksum())
                  .build()
              )
              .toList();
          s3Client.completeMultipartUpload(builder -> builder
              .bucket(zerowebS3Config.getBucket())
              .key(attachment.objectKey())
              .uploadId(s3UploadId.getUploadId())
              .multipartUpload(multipartUpload -> multipartUpload
                  .parts(uploadedParts)
                  .build()
              )
          );
        });
  }
}

