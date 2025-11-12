package io.github.xezzon.zeroweb.attachment.internal;

import com.google.common.hash.Hashing;
import com.google.protobuf.Timestamp;
import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.AttachmentGrpc.AttachmentImplBase;
import io.github.xezzon.zeroweb.attachment.AttachmentItem;
import io.github.xezzon.zeroweb.attachment.AttachmentItem.Builder;
import io.github.xezzon.zeroweb.attachment.AttachmentList;
import io.github.xezzon.zeroweb.attachment.AttachmentStatus;
import io.github.xezzon.zeroweb.attachment.FileMetadata;
import io.github.xezzon.zeroweb.attachment.FileUploadRequest;
import io.github.xezzon.zeroweb.attachment.FileUploadResponse;
import io.github.xezzon.zeroweb.attachment.QueryAttachmentListRequest;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.grpc.stub.StreamObserver;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

/**
 * @author xezzon
 */
@GrpcService
@Slf4j
public class AttachmentGrpcEndpoint extends AttachmentImplBase {

  private final AttachmentService attachmentService;

  public AttachmentGrpcEndpoint(final AttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @Override
  public StreamObserver<FileUploadRequest> uploadFile(
      final StreamObserver<FileUploadResponse> responseObserver) {
    return new StreamObserver<>() {

      private final Attachment attachment = new Attachment();

      @Override
      public void onNext(FileUploadRequest request) {
        FileMetadata metadata = request.getMetadata();
        attachment.setName(metadata.getName());
        attachment.setType(metadata.getType());
        attachment.setBizType(metadata.getBizType());
        attachment.setBizId(metadata.getBizId());
        attachment.setStatus(AttachmentStatusEnum.UPLOADING);

        byte[] chunk = request.getChunk().toByteArray();
        attachment.setSize((long) chunk.length);
        String checksum = Base64.getEncoder()
            .encodeToString(Hashing.sha256().hashBytes(chunk).asBytes());
        attachment.setChecksum(checksum);

        attachmentService.addAttachment(attachment);
        attachmentService.upload(attachment, chunk);
      }

      @Override
      public void onError(Throwable t) {
        log.error("Error during file upload.", t);
        responseObserver.onError(t);
      }

      @Override
      public void onCompleted() {
        attachmentService.updateStatus(attachment.getId());
        responseObserver.onNext(FileUploadResponse.newBuilder()
            .setId(attachment.getId())
            .build()
        );
        responseObserver.onCompleted();
      }
    };
  }

  @Override
  public void queryAttachment(
      final QueryAttachmentListRequest request,
      final StreamObserver<AttachmentList> responseObserver
  ) {
    List<Attachment> attachments = attachmentService
        .queryByBiz(request.getBizType(), request.getBizId());
    List<AttachmentItem> attachmentList = attachments.stream()
        .map(attachment -> {
          Builder builder = AttachmentItem.newBuilder();
          builder
              .setId(attachment.getId())
              .setName(attachment.getName())
              .setChecksum(attachment.getChecksum())
              .setSize(attachment.getSize())
              .setType(attachment.getType())
              .setStatus(AttachmentStatus.valueOf(attachment.getStatus().name()))
              .setCreateTime(Timestamp.newBuilder()
                  .setSeconds(attachment.getCreateTime().getEpochSecond())
                  .setNanos(attachment.getCreateTime().getNano())
                  .build()
              );
          if (attachment.getOwnerId() != null) {
            builder.setOwnerId(attachment.getOwnerId());
          }
          return builder.build();
        })
        .toList();
    responseObserver.onNext(AttachmentList.newBuilder()
        .addAllItems(attachmentList)
        .build()
    );
    responseObserver.onCompleted();
  }
}
