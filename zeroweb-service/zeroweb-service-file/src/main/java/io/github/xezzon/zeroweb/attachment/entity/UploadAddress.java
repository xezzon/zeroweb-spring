package io.github.xezzon.zeroweb.attachment.entity;

import io.github.xezzon.zeroweb.common.config.FileProviderEnum;

/// @author xezzon
public record UploadAddress(
    String id,
    FileProviderEnum provider,
    String endpoint
) {

}
