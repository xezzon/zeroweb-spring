package io.github.xezzon.zeroweb.attachment.event;

import io.github.xezzon.zeroweb.attachment.Attachment;

/**
 * 附件删除事件
 * @param attachment 附件
 * @author xezzon
 */
public record AttachmentDeletedEvent(Attachment attachment) {
}
