package io.github.xezzon.zeroweb.attachment.entity;

import io.github.xezzon.tao.trait.From;
import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.zeroweb.attachment.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/// @author xezzon
/// @param name 文件名
/// @param checksum 文件摘要
/// @param size 文件大小。单位：字节。
/// @param type MIME 类型
/// @param bizType 业务类型
/// @param bizId 业务ID
public record AddAttachmentReq(
    String name,
    String checksum,
    Long size,
    String type,
    String bizType,
    String bizId
) implements Into<Attachment> {

  @Override
  public Attachment into() {
    return Mappers.getMapper(Converter.class).from(this);
  }

  @Mapper
  interface Converter extends From<AddAttachmentReq, Attachment> {

    @Mapping(target = "status", constant = "UPLOADING")
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Override
    Attachment from(AddAttachmentReq source);
  }
}
