package io.github.xezzon.zeroweb.attachment.internal;

import io.github.xezzon.zeroweb.attachment.Attachment;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentReq;
import io.github.xezzon.zeroweb.attachment.entity.AddAttachmentResp;
import io.github.xezzon.zeroweb.attachment.entity.UploadAddress;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  /// 新增附件
  /// @param req 文件信息
  /// @return 文件上传元数据
  @PostMapping()
  public AddAttachmentResp addAttachment(@RequestBody AddAttachmentReq req) {
    Attachment attachment = req.into();
    return attachmentService.addAttachment(attachment);
  }

  /// 获取附件上传地址
  /// @param id 附件ID
  /// @return 上传地址
  @GetMapping("/{id}/endpoint/upload")
  public UploadAddress getUploadAddress(@PathVariable String id) {
    return attachmentService.getUploadAddress(id);
  }

  /// 文件上传完成后，将其状态变更为已完成
  /// @param id 附件ID
  @PutMapping("/{id}/status/done")
  public void finishUpload(@PathVariable String id) {
    attachmentService.updateStatus(id);
  }
}
