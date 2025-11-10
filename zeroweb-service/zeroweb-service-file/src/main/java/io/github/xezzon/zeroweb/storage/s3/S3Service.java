package io.github.xezzon.zeroweb.storage.s3;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.attachment.event.AttachmentCreatedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentUploadedEvent;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.storage.DownloadEndpoint;
import io.github.xezzon.zeroweb.storage.IStorageService;
import io.github.xezzon.zeroweb.storage.StorageContext;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
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
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/// @author xezzon
@Service
@ConditionalOnBean(ZerowebS3Config.class)
public class S3Service implements IStorageService {

  static final String ETAG_CALLBACK_URL = "/s3/{id}/etag";
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
      final S3EtagRepository s3EtagRepository
  ) {
    this.zerowebS3Config = zerowebS3Config;
    this.s3Presigner = s3Presigner;
    this.s3Client = s3Client;
    this.s3UploadIdRepository = s3UploadIdRepository;
    this.s3EtagRepository = s3EtagRepository;
  }

  @Override
  public FileProviderEnum provider() {
    return FileProviderEnum.S3;
  }

  @Override
  public UploadInfo getUploadInfo(Attachment attachment) {
    long partSize = zerowebS3Config.getPartSize();
    int partCount = Math.toIntExact(
        (attachment.getSize() - 1) / partSize + 1
    );
    return new UploadInfo(
        attachment.getId(),
        attachment.getProvider(),
        partCount,
        partSize
    );
  }

  /// 创建预签名的 S3 URL，返回给前端
  public UploadEndpoint getUploadAddress(Attachment attachment) {
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
    return new UploadEndpoint(presignedPutObjectRequest.url().toString());
  }

  /// 创建预签名的 S3 URL，返回给前端
  @Override
  public UploadEndpoint getUploadAddress(Attachment attachment, int partNumber) {
    return this.getUploadAddress(attachment, partNumber, StorageContext.CRC.get());
  }

  public UploadEndpoint getUploadAddress(Attachment attachment, int partNumber, String crc) {
    // 如果分段已上传，则跳过
    Optional<S3Etag> etag = s3EtagRepository
        .findByAttachmentIdAndPartNumber(attachment.getId(), partNumber);
    if (etag.isPresent()) {
      return new UploadEndpoint(partNumber);
    }

    S3UploadId s3UploadId = s3UploadIdRepository.findById(attachment.getId())
        .orElseGet(() -> this.createMultipartUpload(attachment));
    PresignedUploadPartRequest presignedUploadPartRequest = s3Presigner
        .presignUploadPart(presignRequest -> presignRequest
            .signatureDuration(Duration.ofMinutes(10))
            .uploadPartRequest(partRequest -> {
              partRequest
                  .bucket(zerowebS3Config.getBucket())
                  .key(attachment.objectKey())
                  .uploadId(s3UploadId.getUploadId())
                  .partNumber(partNumber);
              if (crc != null) {
                partRequest
                    .checksumAlgorithm(ChecksumAlgorithm.CRC32)
                    .checksumCRC32(crc);
              }
            })
        );
    String callbackUrl = UriComponentsBuilder
        .fromPath(ETAG_CALLBACK_URL)
        .buildAndExpand(attachment.getId())
        .toUriString();
    return new UploadEndpoint(
        partNumber,
        presignedUploadPartRequest.url().toString(),
        callbackUrl
    );
  }

  @Override
  public DownloadEndpoint getDownloadEndpoint(Attachment attachment) {
    PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(
        builder -> builder
            .signatureDuration(Duration.ofHours(12))
            .getObjectRequest(getObject -> getObject
                .bucket(zerowebS3Config.getBucket())
                .key(attachment.objectKey())
                .responseContentType(attachment.getType())
            )
    );
    return new DownloadEndpoint(
        presigned.url().toString()
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

  /// 开启一次分段上传
  /// @param attachment 附件
  /// @return 上传ID
  private S3UploadId createMultipartUpload(Attachment attachment) {
    CreateMultipartUploadResponse response = s3Client.createMultipartUpload(builder -> builder
        .bucket(zerowebS3Config.getBucket())
        .key(attachment.objectKey())
        .contentType(attachment.getType())
        .metadata(Collections.singletonMap("filename", attachment.getName()))
        .checksumType(ChecksumType.FULL_OBJECT)
        .checksumAlgorithm(ChecksumAlgorithm.CRC32)
    );
    String crc = StorageContext.CRC.get();
    S3UploadId s3UploadId = new S3UploadId(attachment.getId(), response.uploadId(), crc);
    s3UploadIdRepository.save(s3UploadId);
    return s3UploadId;
  }

  /// 文件上传前，先调用 S3 开启一次分段上传
  @EventListener
  void listen(AttachmentCreatedEvent event) {
    Attachment attachment = event.attachment();
    if (attachment.getProvider() != FileProviderEnum.S3) {
      return;
    }
    if (attachment.getSize() <= zerowebS3Config.getPartSize()) {
      return;
    }
    this.createMultipartUpload(attachment);
  }

  /// 文件上传后，调用 S3 完成分段合并
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
                  .build()
              )
              .toList();
          s3Client.completeMultipartUpload(builder -> builder
              .bucket(zerowebS3Config.getBucket())
              .key(attachment.objectKey())
              .uploadId(s3UploadId.getUploadId())
              .multipartUpload(multipartUpload -> multipartUpload.parts(uploadedParts))
              .checksumType(ChecksumType.FULL_OBJECT)
              .checksumCRC32(s3UploadId.getCrc())
          );
        });
  }
}

