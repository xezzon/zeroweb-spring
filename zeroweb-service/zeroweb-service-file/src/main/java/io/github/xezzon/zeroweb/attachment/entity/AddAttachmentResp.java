package io.github.xezzon.zeroweb.attachment.entity;

/// @author xezzon
/// @param id 附件ID
/// @param maxPartSize 单个文件最大大小。单位：MB。
public record AddAttachmentResp(
    String id,
    Integer maxPartSize
) {

}
