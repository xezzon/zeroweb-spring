package io.github.xezzon.zeroweb.attachment;

import io.github.xezzon.zeroweb.attachment.enumeration.AttachmentStatusEnum;
import io.github.xezzon.zeroweb.common.config.FileProviderEnum;
import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/// 附件
/// @author xezzon
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_attachment")
@EntityListeners({AuditingEntityListener.class})
public class Attachment {

  /// 附件ID
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /// 文件名
  @Column(name = "name", nullable = false)
  String name;
  /// 文件摘要
  @Column(name = "checksum", nullable = false)
  String checksum;
  /// 文件大小
  ///
  /// 单位：字节
  @Column(name = "size", nullable = false)
  Long size;
  /// MIME 类型
  @Column(name = "type", nullable = false)
  String type;
  /// 业务类型
  @Column(name = "biz_type", nullable = false)
  String bizType;
  /// 业务ID
  @Column(name = "biz_id", length = DatabaseConstant.ID_LENGTH)
  String bizId;
  /// 存储后端
  @Column(name = "provider", nullable = false, updatable = false)
  FileProviderEnum provider;
  /// 附件状态
  @Column(name = "status", nullable = false)
  AttachmentStatusEnum status;
  /// 上传者
  @Column(name = "owner_id", length = DatabaseConstant.ID_LENGTH)
  String ownerId;
  /// 上传时间
  @Column(name = "create_time", nullable = false, updatable = false)
  @CreatedDate
  Instant createTime;
}
