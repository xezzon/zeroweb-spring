package io.github.xezzon.zeroweb.attachment;

/// @author xezzon
public interface IAttachmentService {

  /// 查询附件信息
  /// @param id 附件ID
  /// @return 附件信息
  Attachment queryById(String id);
}
