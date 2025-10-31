package io.github.xezzon.zeroweb.attachment.internal;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo;
import io.github.xezzon.zeroweb.attachment.entity.UploadInfo.Address;
import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.attachment.event.AttachmentCreatedEvent;
import io.github.xezzon.zeroweb.attachment.event.AttachmentUploadedEvent;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.storage.IStorageService;
import io.github.xezzon.zeroweb.storage.file.UploadFileException;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class AttachmentService implements IAttachmentService {

  private final AttachmentRepository attachmentRepository;
  private final ZerowebFileConfig zerowebFileConfig;
  private final IStorageService.Factory storageServiceFactory;
  @Resource
  private ApplicationEventPublisher eventPublisher;

  public AttachmentService(
      final AttachmentRepository attachmentRepository,
      final ZerowebFileConfig zerowebFileConfig,
      IStorageService.Factory storageServiceFactory
  ) {
    this.attachmentRepository = attachmentRepository;
    this.zerowebFileConfig = zerowebFileConfig;
    this.storageServiceFactory = storageServiceFactory;
  }

  @Override
  public Attachment queryById(String id) {
    return attachmentRepository.findById(id).orElseThrow();
  }

  UploadInfo addAttachment(Attachment attachment) {
    attachment.setProvider(zerowebFileConfig.getProvider());
    attachment.setOwnerId(JwtAuth.get()
        .map(JwtClaim::getSub)
        .orElse(null)
    );
    attachmentRepository.save(attachment);
    eventPublisher.publishEvent(new AttachmentCreatedEvent(attachment));
    return this.getUploadAddress(
        attachment.getId(),
        attachment.getChecksum(),
        attachment.getSize()
    );
  }

  UploadInfo getUploadAddress(String id, String checksum, long fileSize) {
    Attachment attachment = attachmentRepository.findById(id).orElseThrow();
    // 断点续传的内容要与之前的内容一致
    if (!Objects.equals(attachment.getChecksum(), checksum)) {
      throw new UploadFileException("Invalid checksum.");
    }
    if (!Objects.equals(attachment.getSize(), fileSize)) {
      throw new UploadFileException("Invalid size.");
    }
    IStorageService storageService = storageServiceFactory.get(attachment.getProvider());
    int partCount = Math.toIntExact(
        (attachment.getSize() - 1) / zerowebFileConfig.getMaxPartSize() + 1
    );
    List<Address> addresses;
    if (partCount == 1) {
      addresses = Collections.singletonList(storageService.getUploadAddress(attachment));
    } else {
      addresses = IntStream.range(0, partCount)
          .mapToObj(i -> storageService.getUploadAddress(attachment, i + 1))
          .toList();
    }
    addresses = addresses.stream().filter(Objects::nonNull).toList();
    return new UploadInfo(
        id,
        attachment.getProvider(),
        addresses,
        partCount,
        zerowebFileConfig.getMaxPartSize()
    );
  }

  void updateStatus(String id) {
    attachmentRepository.findById(id)
        .ifPresent(attachment -> {
          eventPublisher.publishEvent(new AttachmentUploadedEvent(attachment));
          attachment.setStatus(AttachmentStatusEnum.DONE);
          attachmentRepository.save(attachment);
        });
  }

  List<Attachment> queryByBiz(String bizType, String bizId) {
    return attachmentRepository.findByBizTypeAndBizId(bizType, bizId);
  }
}
