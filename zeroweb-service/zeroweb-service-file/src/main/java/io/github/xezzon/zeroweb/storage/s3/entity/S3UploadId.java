package io.github.xezzon.zeroweb.storage.s3.entity;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "zeroweb_s3_upload_id")
public final class S3UploadId {

  /**
   * 附件ID
   */
  @Id
  @Column(
      name = "attachment_id",
      nullable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  private String attachmentId;
  /**
   * S3上传ID
   */
  @Column(name = "upload_id", nullable = false, updatable = false, length = 512)
  private String uploadId;

  public S3UploadId() {
    super();
  }

  public S3UploadId(String attachmentId, String uploadId) {
    this.attachmentId = attachmentId;
    this.uploadId = uploadId;
  }
}
