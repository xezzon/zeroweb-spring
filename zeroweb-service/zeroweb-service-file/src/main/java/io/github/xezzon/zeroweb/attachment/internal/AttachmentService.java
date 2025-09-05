package io.github.xezzon.zeroweb.attachment.internal;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.IAttachmentService;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentResp;
import io.github.xezzon.zeroweb.attachment.entity.UploadAddress;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import io.github.xezzon.zeroweb.storage.IStorageService;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class AttachmentService implements IAttachmentService {

  private final AttachmentRepository attachmentRepository;
  private final ZerowebFileConfig zerowebFileConfig;
  private final IStorageService.Factory storageServiceFactory;

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

  AddAttachmentResp addAttachment(Attachment attachment) {
    attachment.setProvider(zerowebFileConfig.getProvider());
    attachment.setOwnerId(JwtAuth.get()
        .map(JwtClaim::getSub)
        .orElse(null)
    );
    attachmentRepository.save(attachment);
    return new AddAttachmentResp(attachment.getId(), zerowebFileConfig.getMaxPartSize());
  }

  UploadAddress getUploadAddress(String id) {
    Attachment attachment = attachmentRepository.findById(id).orElseThrow();
    IStorageService storageService = storageServiceFactory.get(attachment.getProvider());
    return storageService.getUploadAddress(attachment);
  }
}
