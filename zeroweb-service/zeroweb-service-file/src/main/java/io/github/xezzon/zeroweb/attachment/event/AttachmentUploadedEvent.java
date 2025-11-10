package io.github.xezzon.zeroweb.attachment.event;

import io.github.xezzon.zeroweb.attachment.Attachment;

/**
 * 附件上传完成事件
 * @author xezzon
 */
public record AttachmentUploadedEvent(Attachment attachment) {

}
