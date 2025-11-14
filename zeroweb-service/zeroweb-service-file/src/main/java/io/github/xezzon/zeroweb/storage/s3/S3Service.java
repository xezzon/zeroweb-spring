package io.github.xezzon.zeroweb.storage.s3;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.attachment.event.AttachmentCreatedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentDeletedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentUploadedEvent;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.storage.DownloadEndpoint;
import io.github.xezzon.zeroweb.storage.IStorageService;
import io.github.xezzon.zeroweb.storage.StorageContext;
import io.github.xezzon.zeroweb.storage.UploadEndpoint;
import io.github.xezzon.zeroweb.storage.s3.entity.S3UploadId;
import io.github.xezzon.zeroweb.storage.s3.repository.S3UploadIdRepository;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.ChecksumType;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchUploadException;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/// @author xezzon
@Slf4j
@Service
@ConditionalOnBean(ZerowebS3Config.class)
public class S3Service implements IStorageService {

  private final ZerowebS3Config zerowebS3Config;
  private final S3Presigner s3Presigner;
  private final S3Client s3Client;
  private final S3UploadIdRepository s3UploadIdRepository;

  public S3Service(
      final ZerowebS3Config zerowebS3Config,
      final S3Presigner s3Presigner,
      final S3Client s3Client,
      final S3UploadIdRepository s3UploadIdRepository
  ) {
    this.zerowebS3Config = zerowebS3Config;
    this.s3Presigner = s3Presigner;
    this.s3Client = s3Client;
    this.s3UploadIdRepository = s3UploadIdRepository;
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

    if (partCount > 1) {
      s3UploadIdRepository.findById(attachment.getId()).ifPresentOrElse(
          s3UploadId -> {
            try {
              this.listParts(attachment, s3UploadId.getUploadId());
            } catch (NoSuchUploadException _) {
              // 上传ID已过期，重新创建
              log.warn(
                  "Refresh S3 UploadId {} for attachment {}",
                  s3UploadId.getUploadId(), attachment.getId()
              );
              this.createMultipartUpload(attachment);
            }
          },
          // 如果之前创建上传ID失败，则重新创建
          () -> this.createMultipartUpload(attachment)
      );
    }

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

  public UploadEndpoint getUploadAddress(Attachment attachment, Integer partNumber, String crc) {
    S3UploadId s3UploadId = s3UploadIdRepository.findById(attachment.getId())
        .orElseGet(() -> this.createMultipartUpload(attachment));
    // 如果分段已上传，则跳过
    List<Part> parts = this.listParts(attachment, s3UploadId.getUploadId());
    if (parts.stream()
        .map(Part::partNumber)
        .anyMatch(partNumber::equals)
    ) {
      return new UploadEndpoint(partNumber);
    }

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
    return new UploadEndpoint(
        partNumber,
        presignedUploadPartRequest.url().toString()
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

  /// 开启一次分段上传
  /// @param attachment 附件
  private S3UploadId createMultipartUpload(Attachment attachment) {
    CreateMultipartUploadResponse createMultipartUploadResponse = s3Client.createMultipartUpload(
        builder -> builder
            .bucket(zerowebS3Config.getBucket())
            .key(attachment.objectKey())
            .contentType(attachment.getType())
            .metadata(Collections.singletonMap("filename", attachment.getName()))
            .checksumType(ChecksumType.FULL_OBJECT)
            .checksumAlgorithm(ChecksumAlgorithm.CRC32)
    );
    S3UploadId s3UploadId = s3UploadIdRepository.findById(attachment.getId())
        .orElseGet(() -> new S3UploadId(
            attachment.getId(),
            createMultipartUploadResponse.uploadId(),
            StorageContext.CRC.get()
        ));
    s3UploadId.setUploadId(createMultipartUploadResponse.uploadId());
    s3UploadIdRepository.save(s3UploadId);
    return s3UploadId;
  }

  /**
   * 获取已上传分段情况
   * @param attachment 附件
   * @param uploadId 上传ID
   * @return 已上传分段列表
   * @throws NoSuchUploadException 上传ID已过期或不存在
   */
  private List<Part> listParts(Attachment attachment, String uploadId) throws NoSuchUploadException {
    ListPartsResponse listPartsResponse = s3Client.listParts(builder -> builder
        .bucket(zerowebS3Config.getBucket())
        .key(attachment.objectKey())
        .uploadId(uploadId)
    );
    return listPartsResponse.parts();
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
          List<Part> s3EtagList = this.listParts(attachment, s3UploadId.getUploadId());
          List<CompletedPart> uploadedParts = s3EtagList.stream()
              .map(s3Etag -> CompletedPart.builder()
                  .partNumber(s3Etag.partNumber())
                  .eTag(s3Etag.eTag())
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

  /// 附件删除后，将对应的文件也删除
  @EventListener
  @Async()
  void listen(AttachmentDeletedEvent event) {
    final Attachment attachment = event.attachment();
    s3Client.deleteObject(builder -> builder
        .bucket(zerowebS3Config.getBucket())
        .key(attachment.objectKey())
    );
  }
}

