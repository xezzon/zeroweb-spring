package io.github.xezzon.zeroweb.storage.s3.entity;

import io.github.xezzon.zeroweb.common.constant.DatabaseConstant;
import io.github.xezzon.zeroweb.common.jpa.IdGenerator;
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
@Table(name = "zeroweb_s3_etag")
public class S3Etag {

  @Id
  @IdGenerator
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  private String id;
  /**
   * 附件ID
   */
  @Column(
      name = "attachment_id",
      nullable = false,
      updatable = false,
      length = DatabaseConstant.ID_LENGTH
  )
  private String attachmentId;
  /**
   * 分段序号
   */
  @Column(name = "part_number", nullable = false, updatable = false)
  private Integer partNumber;
  /**
   * S3 ETag
   */
  @Column(name = "etag", nullable = false)
  private String etag;
  /**
   * 分片摘要
   */
  @Column(name = "checksum")
  private String checksum;

  public S3Etag() {
    super();
  }

  public S3Etag(String attachmentId, int partNumber, String etag, String checksum) {
    this.attachmentId = attachmentId;
    this.partNumber = partNumber;
    this.etag = etag;
    this.checksum = checksum;
  }
}
