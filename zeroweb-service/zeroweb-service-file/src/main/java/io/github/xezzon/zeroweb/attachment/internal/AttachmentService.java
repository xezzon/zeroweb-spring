package io.github.xezzon.zeroweb.attachment.internal;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentResp;
import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import io.github.xezzon.zeroweb.auth.JwtAuth;
import io.github.xezzon.zeroweb.auth.JwtClaim;
import io.github.xezzon.zeroweb.common.config.ZerowebFileConfig;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class AttachmentService {

  private final AttachmentRepository attachmentRepository;
  private final ZerowebFileConfig zerowebFileConfig;

  public AttachmentService(
      final AttachmentRepository attachmentRepository,
      final ZerowebFileConfig zerowebFileConfig
  ) {
    this.attachmentRepository = attachmentRepository;
    this.zerowebFileConfig = zerowebFileConfig;
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
}
