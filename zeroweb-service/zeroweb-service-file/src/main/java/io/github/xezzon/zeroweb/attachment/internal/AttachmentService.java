package io.github.xezzon.zeroweb.attachment.internal;

import io.github.xezzon.zeroweb.attachment.repository.AttachmentRepository;
import org.springframework.stereotype.Service;

/// @author xezzon
@Service
public class AttachmentService {

  private final AttachmentRepository attachmentRepository;

  public AttachmentService(
      final AttachmentRepository attachmentRepository
  ) {
    this.attachmentRepository = attachmentRepository;
  }
}
