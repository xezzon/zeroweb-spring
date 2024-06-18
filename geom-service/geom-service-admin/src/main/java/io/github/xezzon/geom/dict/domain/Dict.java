package io.github.xezzon.geom.dict.domain;

import io.github.xezzon.geom.common.constant.DatabaseConstant;
import io.github.xezzon.geom.common.jpa.IEntity;
import io.github.xezzon.geom.common.jpa.IdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
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
@Table(name = "geom_dict")
public class Dict implements IEntity<String> {

  public static final String DICT_TAG = "DICT";

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = DatabaseConstant.ID_LENGTH)
  @IdGenerator
  String id;
  /**
   * 字典目
   */
  @Column(name = "tag", nullable = false, updatable = false)
  String tag;
  /**
   * 字典键
   */
  @Column(name = "code", nullable = false)
  String code;
  /**
   * 字典值
   */
  @Column(name = "label")
  String label;
  /**
   * 排序号
   */
  @Column(name = "ordinal", nullable = false)
  Integer ordinal;
  /**
   * 上级字典ID
   */
  @Column(name = "parent_id", nullable = false)
  String parentId;
  /**
   * 启用状态
   */
  @Column(name = "enabled", nullable = false)
  Boolean enabled;
  /**
   * 子级字典
   */
  @Transient
  List<Dict> children;
}
