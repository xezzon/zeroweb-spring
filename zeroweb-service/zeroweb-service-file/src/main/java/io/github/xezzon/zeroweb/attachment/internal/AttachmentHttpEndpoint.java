package io.github.xezzon.zeroweb.attachment.internal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// @author xezzon
@RestController
@RequestMapping("/attachment")
public class AttachmentHttpEndpoint {

  private final AttachmentService attachmentService;

  public AttachmentHttpEndpoint(
      final AttachmentService attachmentService
  ) {
    this.attachmentService = attachmentService;
  }
}
