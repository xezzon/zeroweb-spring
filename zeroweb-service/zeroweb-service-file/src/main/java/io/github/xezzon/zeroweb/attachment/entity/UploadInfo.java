package io.github.xezzon.zeroweb.attachment.entity;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;

/// 文件上传信息
/// @param id 附件ID
/// @param provider 存储提供商
/// @param partCount 分片数量
/// @param partSize 分片大小。单位 Byte。
/// @author xezzon
public record UploadInfo(
    String id,
    FileProviderEnum provider,
    int partCount,
    int partSize
) {
}
